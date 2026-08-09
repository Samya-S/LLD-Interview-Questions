package controller;

import domain.CashDrawer;
import service.ATMService;

public class ATMController {
    private ATMService atmService;

    public ATMController(ATMService atmService) {
        this.atmService = atmService;
    }

    public void takeOffline(String atmId) {
        atmService.takeOffline(atmId);
    }

    public void bringOnline(String atmId) {
        atmService.bringOnline(atmId);
    }

    public CashDrawer auditCash(String atmId) {
        return atmService.auditCash(atmId);
    }
}
