package com.revature;

enum Transaction_Type{
    deposit, withdraw
}

public class Transaction {
    int id;
    int accountNum;
    String transactionType;
    int amount;

    public Transaction(int id, int accountNum, String transactionType, int amount) {
        this.id = id;
        this.accountNum = accountNum;
        this.transactionType = transactionType;
        this.amount = amount;
    }


    @Override
    public String toString() {
        return "Transaction Number #: " + id +
                ":\tAccount Number = " + accountNum +
                ",\ttransaction Type = " + transactionType +
                ",\tamount = " + amount;
    }
}
