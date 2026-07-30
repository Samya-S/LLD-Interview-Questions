package service;

import domain.Receipt;
import domain.Ticket;

public class ReceiptService {

    public ReceiptService() {
        System.out.println("[SERVICE] ReceiptService initialized");
    }

    public Receipt generateReceipt(Ticket ticket, double fee) {
        System.out.println("[SERVICE] Generating receipt for ticket: " + ticket.getId());

        Receipt receipt = new Receipt(ticket.getId(), fee);
        System.out.println("[SERVICE] Receipt generated: " + receipt.getId() + " with fee: " + fee);

        return receipt;
    }

    public void markReceiptAsPaid(Receipt receipt) {
        System.out.println("[SERVICE] Marking receipt as paid: " + receipt.getId());
        receipt.markAsPaid();
        System.out.println("[SERVICE] Receipt marked as paid successfully");
    }

    public String generateReceiptText(Receipt receipt, Ticket ticket) {
        System.out.println("[SERVICE] Generating receipt text for: " + receipt.getId());

        StringBuilder receiptText = new StringBuilder();
        receiptText.append("📄 Receipt:\n");
        receiptText.append("=== PARKING RECEIPT ===\n");
        receiptText.append("Receipt ID: ").append(receipt.getId()).append("\n");
        receiptText.append("Ticket ID: ").append(ticket.getId()).append("\n");
        receiptText.append("Vehicle: DUMMY-").append(ticket.getId()).append(" (CAR)\n");
        receiptText.append("Entry Time: ").append(ticket.getEntryTime()).append("\n");
        receiptText.append("Exit Time: ").append(receipt.getExitTime()).append("\n");
        receiptText.append("Total Fee: $").append(String.format("%.2f", receipt.getTotalFee())).append("\n");
        receiptText.append("Payment Status: ").append(receipt.getPaymentStatus()).append("\n");
        receiptText.append("=====================\n");

        System.out.println("[SERVICE] Receipt text generated successfully");
        return receiptText.toString();
    }
}