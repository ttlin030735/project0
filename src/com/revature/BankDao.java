package com.revature;

import java.sql.SQLException;
import java.util.List;

public interface BankDao{
    void newAccount(String name, String email, String password, Account_Type type) throws SQLException;
    // customer command
    User getUser(int id, String password, boolean isEmployee) throws SQLException;
    void makeAccount(User customer, Account_Type type) throws SQLException;
    void deposit(User customer, int id, int amount, boolean bypass) throws SQLException;
    void withdraw(User customer, int id, int amount, boolean bypass) throws SQLException;
    void transferMoney(User customer, int yourAccount, int otherAccount, int amount, Money_Transfer moneyTransfer) throws SQLException;
    //employee command
    List<Account> viewPending() throws SQLException;
    void accountApproval(Account account) throws SQLException;
    List<Customer> getCustomer() throws SQLException;
    List<Account> getAccount() throws SQLException;
    List<Account> getCustomerAccount(int customerID) throws SQLException;
    List<Transaction> viewTransaction() throws SQLException;
    List<MoneyTransfer> viewMoneyTransferList() throws SQLException;
    void approveTransfer(User user, int transferID, boolean accept) throws SQLException;
    void inputTransaction(int accountID, int amount, Transaction_Type isWithdrawal) throws SQLException;
    void clearList(String list) throws SQLException;
    void databaseReset() throws SQLException;
}
