package com.revature;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BankDaoImpl implements BankDao{
    Connection connection;

    public BankDaoImpl(){ this.connection = ConnectionFactory.getConnection();}

    @Override
    public User getUser(int id, String password, boolean isEmployee) throws SQLException {
        User user;
        String column;
        String userType;
        if(isEmployee) {
            user = new Employee();
            column = "Employee";
            userType = "employee";
        }
        else {
            user = new Customer();
            column = "Customer";
            userType = "customer";
        }
        user.setId(id);
        user.setPassword(password);
        String sql = "SELECT * FROM " + userType + " WHERE " + column + "_ID = " + user.getId() + " AND Password = '" + user.getPassword() + "'";
        ResultSet resultSet = getQuery(sql);
        if(resultSet.next()){
            user.setId(resultSet.getInt(1));
            user.setPassword(resultSet.getString(2));
            user.setName(resultSet.getString(3));
            user.setEmail(resultSet.getString(4));
            return user;
        }
        return null;
    }
    @Override
    public void newAccount(String name, String email, String password, Account_Type type) throws SQLException {
        String sql;
        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);
        customer.setPassword(password);
        sql = "INSERT INTO customer(Name, Email, Password) VALUES (?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, customer.getName());
        preparedStatement.setString(2, customer.getEmail());
        preparedStatement.setString(3, customer.getPassword());
        if(preparedStatement.executeUpdate() > 0){
            System.out.println("New customer created!");
            sql = "Select * FROM customer WHERE Name = '" + customer.getName() +"'";
            ResultSet resultSet = getQuery(sql);
            if(resultSet.next()) {
                int id = resultSet.getInt(1);
                System.out.println("Your customer ID: " + id);
                customer.setId(id);
                makeAccount(customer, type);
                System.out.println("Please wait until an employee approve of your bank account!");
            }
        }
        else System.out.println("Customer name/email already exist");
    }
    @Override
    public void makeAccount(User customer, Account_Type type) throws SQLException {
        String sql = "INSERT INTO account(Customer_ID, Account_Type, Amount, Daily_Limit, Status) VALUES (?,?,?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, customer.getId());
        preparedStatement.setString(2, String.valueOf(type));
        preparedStatement.setInt(3, 0);
        preparedStatement.setInt(4, 0);
        preparedStatement.setString(5, String.valueOf(Status.pending));
        if(preparedStatement.executeUpdate() > 0) System.out.println("Account waiting for approval!");
        else System.out.println("Please try again later");
    }
    @Override
    public void deposit(User customer, int id, int amount, boolean bypass) throws SQLException {
        if(amount < 0) System.out.println("Please enter an amount larger than 0!");
        else {
            int pass;
            if(bypass) pass = 1;
            else pass = 0;
            String sql = "CALL deposit(?, ?, ?, ?, ?)";
            CallableStatement callableStatement = connection.prepareCall(sql);
            callableStatement.setInt(1, customer.getId());
            callableStatement.setInt(2, id);
            callableStatement.setInt(3, amount);
            callableStatement.setInt(4, pass);
            callableStatement.registerOutParameter(5, Types.VARCHAR);
            callableStatement.execute();
            System.out.println(callableStatement.getString(5));

        }
    }
    @Override
    public void withdraw(User customer, int id, int amount, boolean bypass) throws SQLException {
        if(amount < 0) System.out.println("Please enter an amount larger than 0!");
        else {
            //Account account = new Account();
            int pass;
            if(bypass) pass = 1;
            else pass = 0;
            String sql = "CALL withdraw(?, ?, ?, ?, ?)";
            CallableStatement callableStatement = connection.prepareCall(sql);
            callableStatement.setInt(1, customer.getId());
            callableStatement.setInt(2, id);
            callableStatement.setInt(3, amount);
            callableStatement.setInt(4, pass);
            callableStatement.registerOutParameter(5, Types.VARCHAR);
            callableStatement.execute();
            System.out.println(callableStatement.getString(5));
        }
    }
    @Override
    public void transferMoney(User customer, int yourAccount, int otherAccount, int amount, Money_Transfer moneyTransfer) throws SQLException {
        String sql;
        sql = "SELECT * FROM account WHERE Account_Number = " + yourAccount + " AND Customer_ID = " + customer.getId();
        ResultSet resultSet = getQuery(sql);
        if(resultSet.next()) {
            sql = "INSERT INTO money_transfer(Starting_Account, type, Amount, Ending_Account) VALUES (?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, yourAccount);
            preparedStatement.setString(2, String.valueOf(moneyTransfer));
            preparedStatement.setInt(3, amount);
            preparedStatement.setInt(4, otherAccount);
            if (preparedStatement.executeUpdate() > 0) System.out.println("Money transfer is now pending!");
            else System.out.println("Something went wrong!");
        }
        else System.out.println("At least 1 of your account must be involved!");
    }
    @Override
    public List<Account> viewPending() throws SQLException{
        List<Account> pendingAccounts = new ArrayList<>();
        String sql = "SELECT * FROM account WHERE Status = 'pending'";
        ResultSet resultSet = getQuery(sql);

        while(resultSet.next()){
            int id = resultSet.getInt(1);
            int customerID = resultSet.getInt(2);
            Account_Type type = Account_Type.valueOf(resultSet.getString(3));
            int amount = resultSet.getInt(4);
            int limit = resultSet.getInt(5);
            Status status = Status.valueOf(resultSet.getString(6));
            pendingAccounts.add(new Account(id, customerID, type, amount, limit, status));
        }

        return pendingAccounts;
    }
    @Override
    public void accountApproval(Account account) throws SQLException {
        String sql;
        boolean accountExist = false;
        List<Account> accountList = new ArrayList<>();
        sql = "Select * FROM account WHERE Status = 'pending'";
        ResultSet resultSet = getQuery(sql);

        while(resultSet.next()) {
            Account newAccount = new Account();
            newAccount.setAccountNumber(resultSet.getInt(1));
            newAccount.setStatus(Status.valueOf(resultSet.getString(6)));
            accountList.add(newAccount);
        }

        for (Account value : accountList) {
            if (account.getAccountNumber() == value.getAccountNumber()) {
                if (account.getStatus() == Status.approved) {
                    if(account.getAccountType() == Account_Type.saving){
                        System.out.print("Please enter a daily limit for the customer: ");
                        int limit = Main.getNumber();
                        sql = "UPDATE account SET Status = ? , Daily_Limit " + limit + " WHERE Account number";
                    }
                    else  sql = "UPDATE account SET Status = ? WHERE Account_Number = ?";
                }
                else sql = "UPDATE account SET status = ?, Daily_Limit = 0 WHERE Account_Number = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, account.getStatus().toString());
                preparedStatement.setInt(2, account.getAccountNumber());
                if (preparedStatement.executeUpdate() > 0)
                    System.out.println("Account Number #" + account.getAccountNumber() + " have been " + account.getStatus() + "!");
                accountExist = true;
            }
        }
        if(!accountExist) System.out.println("Account not found in pending!");
    }
    @Override
    public List<Customer> getCustomer() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customer";
        ResultSet resultSet = getQuery(sql);
        while(resultSet.next()){
            int id = resultSet.getInt(1);
            String password = resultSet.getString(2);
            String name = resultSet.getString(3);
            String email = resultSet.getString(4);
            customers.add(new Customer(id, name, email, password));
        }
        return customers;
    }
    @Override
    public List<Account> getAccount() throws SQLException{
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM account";
        ResultSet resultSet = getQuery(sql);
        while(resultSet.next()){
            int id = resultSet.getInt(1);
            int customerID = resultSet.getInt(2);
            Account_Type type = Account_Type.valueOf(resultSet.getString(3));
            int amount = resultSet.getInt(4);
            int limit = resultSet.getInt(5);
            Status status = Status.valueOf(resultSet.getString(6));
            accounts.add(new Account(id, customerID, type, amount, limit, status));
        }
        return accounts;
    }
    @Override
    public List<Account> getCustomerAccount(int customerID) throws SQLException {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM account WHERE Customer_ID = " + customerID;
        ResultSet resultSet = getQuery(sql);
        while(resultSet.next()){
            int id = resultSet.getInt(1);
            Account_Type type = Account_Type.valueOf(resultSet.getString(3));
            int amount = resultSet.getInt(4);
            int limit = resultSet.getInt(5);
            Status status = Status.valueOf(resultSet.getString(6));
            accounts.add(new Account(id, customerID, type, amount, limit, status));
        }
        return accounts;
    }
    @Override
    public List<Transaction> viewTransaction() throws SQLException{
        List<Transaction> transactionList = new ArrayList<>();
        String sql = "SELECT * FROM transaction";
        ResultSet resultSet = getQuery(sql);
        while(resultSet.next()){
            int id = resultSet.getInt(1);
            int account = resultSet.getInt(2);
            String transaction = resultSet.getString(3);
            int amount = resultSet.getInt(4);
            transactionList.add(new Transaction(id, account, transaction, amount));
        }
        return transactionList;
    }
    @Override
    public List<MoneyTransfer> viewMoneyTransferList(User user, int id) throws SQLException {
        String sql = "SELECT * FROM account WHERE Customer_ID = " + user.getId() + " AND Account_Number = " + id;
        List<MoneyTransfer> transferList = new ArrayList<>();
        ResultSet resultSet = getQuery(sql);
        while(resultSet.next()){
            sql = "SELECT * FROM money_transfer WHERE Starting_Account = " + id + " OR Ending_Account = " + id;
            resultSet = getQuery(sql);
            while(resultSet.next()){
                int transferID = resultSet.getInt(1);
                int accountID = resultSet.getInt(2);
                Money_Transfer moneyTransfer = Money_Transfer.valueOf(resultSet.getString(3));
                int amount = resultSet.getInt(4);
                int otherID = resultSet.getInt(5);
                transferList.add(new MoneyTransfer(transferID, accountID, moneyTransfer, amount, otherID));
            }
        }
        return transferList;
    }
    @Override
    public void inputTransaction(int accountId, int amount, Transaction_Type transaction) throws SQLException{
        String sql;
        sql = "INSERT INTO transaction(Account_Number, Transaction, Amount) VALUES (?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, accountId);
        preparedStatement.setString(2, String.valueOf(transaction));
        preparedStatement.setInt(3, amount);
        if(preparedStatement.executeUpdate() > 0) System.out.println("Transaction Recorded");
    }
    @Override
    public void approveTransfer(User user, int transferID, boolean accept) throws SQLException{
        String sql = "SELECT * FROM money_transfer WHERE Transfer_ID = " + transferID;
        ResultSet resultSet = getQuery(sql);
        if(resultSet.next()){
            int otherParty = resultSet.getInt(2);
            int yourAccount = resultSet.getInt(5);
            Money_Transfer transferType = Money_Transfer.valueOf(resultSet.getString(3));
            int amount = resultSet.getInt(4);
            sql = "SELECT * FROM account WHERE Customer_ID = " + user.getId() + " AND Account_Number = " + yourAccount;
            resultSet = getQuery(sql);
            if(resultSet.next()){
                if(accept){
                    int receiverID;
                    int senderID;
                    if(transferType == Money_Transfer.request){
                        receiverID = otherParty;
                        senderID = yourAccount;
                    }
                    else {
                        receiverID = yourAccount;
                        senderID = otherParty;
                    }
                    deposit(user, receiverID, amount, true);
                    withdraw(user, senderID, amount, true);
                    System.out.println("Money transfer accepted!");
                }
                else System.out.println("Money transfer decline!");

                sql = "DELETE FROM money_transfer WHERE Transfer_ID = " + transferID;
                if(getUpdate(sql) > 0) System.out.println("Resolving money transfer request.");
            }
            else System.out.println("You are not the target of a money transfer!");
        }
        else System.out.println("Money transfer request not found!");

    }
    @Override
    public void clearList(String list) throws SQLException{
        String input;
        String table = list.toLowerCase();
        String output;
        if(list.equals("Withdrawal")){
            input = "Account_Number";
            output = "Daily Withdraw Limit Reset";
        }
        else{
            input = list + "_ID";
            output = "Transactions History Deleted";
        }

        String sql = "SELECT COUNT("+ input +") FROM transaction";
        ResultSet resultSet = getQuery(sql);
        resultSet.next();
        if(resultSet.getInt(1) == 0) System.out.println("There is no history!");
        else{
            sql = "TRUNCATE TABLE " + table;
            getUpdate(sql);
            System.out.println(output);
        }
    }
    @Override
    public void databaseReset() throws SQLException{
        String sql = "CALL database_Reset()";
        if(getUpdate(sql) > 0) System.out.println("Database have now been reset!");
    }
    private ResultSet getQuery(String sql) throws SQLException{
        Statement statement = connection.createStatement();
        return statement.executeQuery(sql);
    }
    private int getUpdate(String sql) throws SQLException{
        Statement statement = connection.createStatement();
        return statement.executeUpdate(sql);
    }
}
