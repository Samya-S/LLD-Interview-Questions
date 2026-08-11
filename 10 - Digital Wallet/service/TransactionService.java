package service;

import domain.*;
import repository.TransactionRepository;
import repository.WalletRepository;
import service.gateway.PaymentGatewayProvider;
import service.gateway.PaymentGatewayRouter;
import service.notification.NotificationMessage;
import service.notification.NotificationRouter;

import java.util.*;

public class TransactionService {
    private TransactionRepository transactionRepository;
    private WalletRepository walletRepository;
    private LockService lockService;
    private PaymentGatewayRouter paymentGatewayRouter;
    private NotificationRouter notificationRouter;

    public TransactionService(TransactionRepository transactionRepository,
                              WalletRepository walletRepository,
                              LockService lockService,
                              PaymentGatewayRouter paymentGatewayRouter,
                              NotificationRouter notificationRouter) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.lockService = lockService;
        this.paymentGatewayRouter = paymentGatewayRouter;
        this.notificationRouter = notificationRouter;
    }

    public Transaction transfer(String fromAccountNumber, String toAccountNumber, long amountMinor, String description) {
        if (fromAccountNumber == null || toAccountNumber == null) {
            throw new IllegalArgumentException("Account numbers cannot be null");
        }
        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        if (amountMinor <= 0) {
            throw new IllegalArgumentException("Amount must be positive (minor units)");
        }

        Wallet fromWallet = null;
        Wallet toWallet = null;
        String lockKey1 = null;
        String lockKey2 = null;
        List<String> sortedKeys = null;
        boolean firstAcquired = false;
        boolean secondAcquired = false;

        try {
            // Resolve wallets to compute lock keys
            fromWallet = getActiveWalletOrThrow(fromAccountNumber);
            toWallet = getActiveWalletOrThrow(toAccountNumber);

            lockKey1 = "wallet_lock_" + fromWallet.getId();
            lockKey2 = "wallet_lock_" + toWallet.getId();

            // Acquire locks in sorted order to avoid deadlocks
            sortedKeys = new ArrayList<>(List.of(lockKey1, lockKey2));
            Collections.sort(sortedKeys);

            if (lockService.acquire(sortedKeys.get(0), 5000)) {
                firstAcquired = true;
            } else {
                throw new IllegalStateException("Failed to acquire lock");
            }

            if (lockService.acquire(sortedKeys.get(1), 5000)) {
                secondAcquired = true;
            } else {
                // release first if second fails
                lockService.release(sortedKeys.get(0));
                firstAcquired = false;
                throw new IllegalStateException("Failed to acquire lock");
            }

            // Re-fetch wallets under lock to ensure latest state
            fromWallet = walletRepository.findById(fromWallet.getId())
                    .orElseThrow(() -> new IllegalStateException("Source wallet not found"));
            toWallet = walletRepository.findById(toWallet.getId())
                    .orElseThrow(() -> new IllegalStateException("Destination wallet not found"));

            if (fromWallet.getStatus() != WalletStatus.ACTIVE || toWallet.getStatus() != WalletStatus.ACTIVE) {
                throw new IllegalStateException("One or more wallets are not ACTIVE");
            }

            // Validate sufficient balance
            if (fromWallet.getBalanceMinor() < amountMinor) {
                throw new IllegalStateException("Insufficient balance");
            }

            // Apply balance updates
            fromWallet.setBalanceMinor(fromWallet.getBalanceMinor() - amountMinor);
            toWallet.setBalanceMinor(toWallet.getBalanceMinor() + amountMinor);
            fromWallet.setUpdatedAt(System.currentTimeMillis());
            toWallet.setUpdatedAt(System.currentTimeMillis());
            walletRepository.save(fromWallet);
            walletRepository.save(toWallet);

            // Record transaction
            Transaction tx = new Transaction(
                    UUID.randomUUID().toString(),
                    fromWallet.getId(),
                    toWallet.getId(),
                    amountMinor,
                    TransactionType.TRANSFER,
                    TransactionStatus.COMPLETED,
                    null,
                    description,
                    System.currentTimeMillis()
            );
            tx = transactionRepository.save(tx);

            // Notify (email)
            // TODO: Populate correct recipient email (lookup from UserRepository if needed)
            notificationRouter.send("email",
                    new NotificationMessage("user@example.com", "Transfer Completed",
                            "Transfer of " + amountMinor + " minor units from " + fromAccountNumber +
                                    " to " + toAccountNumber + " completed."));

            return tx;
        } finally {
            if (secondAcquired) {
                lockService.release(sortedKeys.get(1));
            }
            if (firstAcquired) {
                lockService.release(sortedKeys.get(0));
            }
        }
    }

    public Transaction initiateDeposit(String accountNumber, long amountMinor, String paymentMethod,
                                       String paymentGateway, Map<String, String> paymentDetails) {
        if (amountMinor <= 0) {
            throw new IllegalArgumentException("Amount must be positive (minor units)");
        }
        Wallet wallet = getActiveWalletOrThrow(accountNumber);

        String selected = paymentGatewayRouter.selectProvider(paymentGateway, amountMinor, "TUF"); // Config 
        PaymentGatewayProvider provider = paymentGatewayRouter.resolve(selected);
        String providerRef = provider.initiatePayment(accountNumber, amountMinor, paymentMethod, paymentDetails);

        Transaction tx = new Transaction(
                UUID.randomUUID().toString(), 
                null,
                wallet.getId(),
                amountMinor,
                TransactionType.DEPOSIT,
                TransactionStatus.PENDING,
                providerRef,
                "Deposit via " + selected,
                System.currentTimeMillis()
        );
        return transactionRepository.save(tx);
    }

    public void handleDepositCallback(String providerRef, TransactionStatus status) {
        Optional<Transaction> txOpt = transactionRepository.findByProviderRef(providerRef);
        if (txOpt.isEmpty()) {
            throw new IllegalArgumentException("Transaction not found for providerRef: " + providerRef);
        }
        Transaction tx = txOpt.get();

        if (tx.getStatus() == TransactionStatus.COMPLETED || tx.getStatus() == TransactionStatus.FAILED) {
            return; // already processed
        }

        // Lock on destination wallet to avoid concurrent credits
        String walletId = tx.getToWalletId();
        String lockKey = "wallet_lock_" + walletId;
        if (!lockService.acquire(lockKey, 5000)) {
            throw new IllegalStateException("Failed to acquire lock for wallet " + walletId);
        }
        try {
            Optional<Wallet> walletOpt = walletRepository.findById(walletId);
            if (walletOpt.isEmpty()) {
                throw new IllegalStateException("Wallet not found for transaction");
            }
            Wallet wallet = walletOpt.get();

            if (status == TransactionStatus.COMPLETED) {
                wallet.setBalanceMinor(wallet.getBalanceMinor() + tx.getAmountMinor());
                wallet.setUpdatedAt(System.currentTimeMillis());
                walletRepository.save(wallet);
                tx.setStatus(TransactionStatus.COMPLETED);

                // Notify
                // TODO: Lookup real email for wallet owner
                notificationRouter.send("email",
                        new NotificationMessage("user@example.com", "Deposit Completed",
                                "Deposit of " + tx.getAmountMinor() + " minor units to account credited."));
            } else {
                tx.setStatus(TransactionStatus.FAILED);
            }
            transactionRepository.save(tx);
        } finally {
            lockService.release(lockKey);
        }
    }

    public Transaction withdraw(String accountNumber, long amountMinor, String description) {
        if (amountMinor <= 0) {
            throw new IllegalArgumentException("Amount must be positive (minor units)");
        }
        // Use the same locking mechanism in this one as well 
        Wallet wallet = getActiveWalletOrThrow(accountNumber);
        if (wallet.getBalanceMinor() < amountMinor) {
            throw new IllegalStateException("Insufficient balance");
        }


        // Create PENDING withdrawal; an external payout service would complete it
        Transaction tx = new Transaction(
                UUID.randomUUID().toString(),
                wallet.getId(),
                null,
                amountMinor,
                TransactionType.WITHDRAWAL,
                TransactionStatus.PENDING,
                null,
                description,
                System.currentTimeMillis()
        );
        tx = transactionRepository.save(tx);

        
        // TODO: Integrate with payout provider to process withdrawal by fetching user's bank details

        return tx;
    }

    public AccountStatement getStatement(String accountNumber, Long startUtc, Long endUtc) {
        Wallet wallet = getActiveOrClosedWallet(accountNumber);
        long start = (startUtc == null) ? 0L : startUtc;
        long end = (endUtc == null) ? Long.MAX_VALUE : endUtc;
        List<Transaction> txs = transactionRepository.findByWalletAndRange(wallet.getId(), start, end);
        return new AccountStatement(wallet.getId(), wallet.getAccountNumber(), txs, startUtc, endUtc, wallet.getBalanceMinor());
    }

    private Wallet getActiveWalletOrThrow(String accountNumber) {
        Wallet wallet = walletRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for account: " + accountNumber));
        if (wallet.getStatus() != WalletStatus.ACTIVE) {
            throw new IllegalStateException("Wallet is not ACTIVE: " + wallet.getStatus());
        }
        return wallet;
    }

    private Wallet getActiveOrClosedWallet(String accountNumber) {
        return walletRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for account: " + accountNumber));
    }
}

