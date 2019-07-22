package com.example.demo.enums;

public enum TransferStatus {
    OPENED("OPENED"),
    FINISHED("FINISHED"),
    CANCELED("CANCELED");

    private final String statusTransfer;

    TransferStatus(String statusTransfer)
    {
        this.statusTransfer = statusTransfer;
    }

    public String getValue() {
        return statusTransfer;
    }
}
