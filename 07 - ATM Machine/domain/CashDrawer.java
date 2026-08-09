package domain;

import java.util.HashMap;
import java.util.Map;

public class CashDrawer {
    private String atmId;
    private Map<Denomination, Integer> notesByDenomination;

    public CashDrawer(String atmId) {
        this.atmId = atmId;
        this.notesByDenomination = new HashMap<>();
        // Initialize with zero notes
        for (Denomination denom : Denomination.values()) {
            notesByDenomination.put(denom, 0);
        }
    }

    public String getAtmId() {
        return atmId;
    }

    public void setAtmId(String atmId) {
        this.atmId = atmId;
    }

    public Map<Denomination, Integer> getNotesByDenomination() {
        return notesByDenomination;
    }

    public void setNotesByDenomination(Map<Denomination, Integer> notesByDenomination) {
        this.notesByDenomination = notesByDenomination;
    }

    public void addNotes(Denomination denomination, int count) {
        int current = notesByDenomination.getOrDefault(denomination, 0);
        notesByDenomination.put(denomination, current + count);
    }

    public void removeNotes(Denomination denomination, int count) {
        int current = notesByDenomination.getOrDefault(denomination, 0);
        notesByDenomination.put(denomination, Math.max(0, current - count));
    }

    public int getTotalCash() {
        return notesByDenomination.entrySet().stream()
                .mapToInt(entry -> entry.getKey().getValue() * entry.getValue())
                .sum();
    }
}
