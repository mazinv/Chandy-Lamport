package cz.zcu.kiv.ds.mazin.messaging;

public enum MessageType {
    CREDIT("CREDIT"),
    DEBIT("DEBIT"),
    MARKER("MARKER");

    private final String name;

    private MessageType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
