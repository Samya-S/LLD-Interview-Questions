package controller;

import domain.Receipt;
import domain.Ticket;
import service.PaymentService;
import service.PricingService;
import service.ReceiptService;
import service.SlotService;
import service.TicketService;
import java.util.Optional;
import java.util.UUID;

public class ExitController {
    private TicketService ticketService;
    private PricingService pricingService;
    private PaymentService paymentService;
    private ReceiptService receiptService;
    private SlotService slotService;

    public ExitController(TicketService ticketService, PricingService pricingService,
                         PaymentService paymentService, ReceiptService receiptService,
                         SlotService slotService) {
        this.ticketService = ticketService;
        this.pricingService = pricingService;
        this.paymentService = paymentService;
        this.receiptService = receiptService;
        this.slotService = slotService;
        System.out.println("[CONTROLLER] ExitController initialized");
    }

    public ExitResult exitVehicle(UUID ticketId) {
        System.out.println("[CONTROLLER] Vehicle exit request - Ticket: " + ticketId);
        
        try {
            // Retrieve ticket
            Optional<Ticket> ticketOpt = ticketService.getTicket(ticketId);
            if (ticketOpt.isEmpty()) {
                return new ExitResult(false, null, 0.0, "Ticket not found");
            }
            
            Ticket ticket = ticketOpt.get();
            if (!ticket.isActive()) {
                return new ExitResult(false, null, 0.0, "Ticket is not active");
            }
            
            // Calculate fee
            double fee = pricingService.calculateFee(ticket);
            System.out.println("[CONTROLLER] Fee calculated: " + fee);
            
            // Process payment with retry
            boolean paymentSuccess = paymentService.processPaymentWithRetry(ticketId, fee, 3);
            if (!paymentSuccess) {
                return new ExitResult(false, null, fee, "Payment failed");
            }
            
            // Generate receipt
            Receipt receipt = receiptService.generateReceipt(ticket, fee);
            receiptService.markReceiptAsPaid(receipt);
            
            // Release slot
            slotService.releaseSlot(ticket.getSlotId());
            
            // Deactivate ticket
            ticketService.deactivateTicket(ticketId);
            
            System.out.println("[CONTROLLER] Vehicle exit successful - Receipt: " + receipt.getId());
            return new ExitResult(true, receipt.getId(), fee, "Exit successful");
            
        } catch (Exception e) {
            System.out.println("[CONTROLLER] Vehicle exit failed: " + e.getMessage());
            return new ExitResult(false, null, 0.0, e.getMessage());
        }
    }

    public String generateReceiptText(UUID ticketId) {
        System.out.println("[CONTROLLER] Generating receipt text for ticket: " + ticketId);
        
        try {
            Optional<Ticket> ticketOpt = ticketService.getTicket(ticketId);
            if (ticketOpt.isEmpty()) {
                return "Ticket not found";
            }
            
            Ticket ticket = ticketOpt.get();
            double fee = pricingService.calculateFee(ticket);
            Receipt receipt = receiptService.generateReceipt(ticket, fee);
            
            String receiptText = receiptService.generateReceiptText(receipt, ticket);
            System.out.println("[CONTROLLER] Receipt text generated successfully");
            return receiptText;
            
        } catch (Exception e) {
            System.out.println("[CONTROLLER] Receipt text generation failed: " + e.getMessage());
            return "Error generating receipt: " + e.getMessage();
        }
    }

    public static class ExitResult {
        private final boolean success;
        private final UUID receiptId;
        private final double fee;
        private final String message;

        public ExitResult(boolean success, UUID receiptId, double fee, String message) {
            this.success = success;
            this.receiptId = receiptId;
            this.fee = fee;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public UUID getReceiptId() { return receiptId; }
        public double getFee() { return fee; }
        public String getMessage() { return message; }
    }
} 