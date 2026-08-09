package controller;

import domain.ATM;
import domain.Session;
import domain.Transaction;
import domain.TransactionType;
import domain.Denomination;
import service.TransactionService;
import service.SessionService;
import service.ATMService;
import java.util.Map;
import domain.exception.InvalidATMOperationException;

public class TransactionController {
    private TransactionService transactionService; // kept for future direct access if needed
    private SessionService sessionService;
    private ATMService atmService;

    public TransactionController(TransactionService transactionService,
            SessionService sessionService,
            ATMService atmService) {
        this.transactionService = transactionService;
        this.sessionService = sessionService;
        this.atmService = atmService;
    }

    public Transaction showBalance(String sessionId) {
        Session session = sessionService.getSession(sessionId);
        if (session == null)
            return null;
        ATM atm = atmService.getATM(session.getAtmId());
        if (atm == null)
            return null;
        try {
            atm.selectTransaction(TransactionType.BALANCE);
            atm.processTransaction(0);
            return atm.getLastTransaction();
        } catch (InvalidATMOperationException ex) {
            System.out.println("[ERROR] " + ex.getMessage());
            return null;
        }
    }

    public Transaction withdrawCash(String sessionId, long amountMinorUnits) {
        Session session = sessionService.getSession(sessionId);
        if (session == null)
            return null;
        ATM atm = atmService.getATM(session.getAtmId());
        if (atm == null)
            return null;
        try {
            atm.selectTransaction(TransactionType.WITHDRAW);
            atm.processTransaction(amountMinorUnits);
            return atm.getLastTransaction();
        } catch (InvalidATMOperationException ex) {
            System.out.println("[ERROR] " + ex.getMessage());
            return null;
        }
    }

    public Transaction depositCash(String sessionId, Map<Denomination, Integer> notes) {
        Session session = sessionService.getSession(sessionId);
        if (session == null)
            return null;
        ATM atm = atmService.getATM(session.getAtmId());
        if (atm == null)
            return null;
        long amount = calculateAmount(notes);
        try {
            atm.selectTransaction(TransactionType.DEPOSIT);
            atm.processTransaction(amount, notes);
            return atm.getLastTransaction();
        } catch (InvalidATMOperationException ex) {
            System.out.println("[ERROR] " + ex.getMessage());
            return null;
        }
    }

    private long calculateAmount(Map<Denomination, Integer> notes) {
        if (notes == null)
            return 0;
        return notes.entrySet().stream()
                .mapToLong(entry -> entry.getKey().getValue() * entry.getValue())
                .sum();
    }
}