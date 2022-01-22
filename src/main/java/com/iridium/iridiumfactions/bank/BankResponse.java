package com.iridium.iridiumfactions.bank;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BankResponse {
    private double amount;
    private BankResponseType bankResponseType;

    public enum BankResponseType {
        SUCCESS, INSUFFICIENT_FUNDS
    }
}
