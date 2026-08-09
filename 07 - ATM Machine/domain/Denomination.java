package domain;

public enum Denomination {
    FIVE_HUNDRED(50000), // $500 in cents
    TWO_HUNDRED(20000), // $200 in cents
    ONE_HUNDRED(10000); // $100 in cents

    private final int value;

    Denomination(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
