package com.revature;

enum Status{
    pending, approved, declined
}
enum Account_Type{
    checking, saving
}

public class Account {
    private int accountNumber;
    private int customerID;
    private Account_Type accountType;
    private int amount;
    private int limit;
    private Status status;

    public Account() {}

    public Account(int accountNumber, int customerID, Account_Type accountType, int amount, int limit, Status status) {
        this.accountNumber = accountNumber;
        this.customerID = customerID;
        this.accountType = accountType;
        this.amount = amount;
        this.limit = limit;
        this.status = status;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setAccountType(Account_Type accountType) {
        this.accountType = accountType;
    }

    public Account_Type getAccountType() {
        return accountType;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Status getStatus() {
        return status;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setStatus(Status status){this.status = status;}
    public void setStatus(Boolean status){
        if(status) this.status = Status.approved;
        else this.status = Status.declined;
    }

    @Override
    public String toString() {
        return "Account Number = " + accountNumber +
                "\tCustomer Number = " + customerID +
                "\taccountType = " + accountType +
                "\tamount = " + amount +
                "\tlimit = " + limit +
                "\tstatus = " + status;
    }
}
