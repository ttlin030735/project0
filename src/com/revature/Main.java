package com.revature;

import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        BankDao dao = BankDaoFactory.getBankDao();

        boolean run = true;
        System.out.println("Welcome!");
        while(run) {
            System.out.println("\nPlease choose of the following options: ");
            System.out.println("1)Login\n2)Create customer account\n3)Reset Database(no warning)\n4)Exit");
            int input = getNumber();
            switch (input) {
                case 1 -> {
                    boolean login = true;
                    while (login) {
                        System.out.println("\nAre you logging in as a customer or employee?");
                        System.out.println("1)Customer\n2)Employee\n3)Go previous screen");
                        input = getNumber();
                        switch (input) {
                            case 1 -> {
                                System.out.println("Customer Login!");
                                boolean customerLogin = false;
                                User customer;
                                System.out.print("Please enter customer id: ");
                                int customerId = getNumber();
                                System.out.print("Please enter the password: ");
                                String password = scanner.next();
                                customer = dao.getUser(customerId, password, false);
                                if(customer != null){
                                    System.out.println("Login in as " + customer.getName() +"!");
                                    customerLogin = true;
                                }
                                else System.out.println("Invalid login returning to previous screen!");
                                while(customerLogin){

                                    System.out.println("\nChoose 1 of the following!");
                                    System.out.println("1)View you accounts");
                                    System.out.println("2)Making a new bank account");
                                    System.out.println("3)Deposit money into an account");
                                    System.out.println("4)Withdraw money from an account");
                                    System.out.println("5)Transfer money");
                                    System.out.println("6)Viewing pending money transfer you are in");
                                    System.out.println("7)Accepting/declining money transfer if you are second party");
                                    System.out.println("8)Log out");

                                    input = getNumber();
                                    switch(input) {
                                        case 1 ->{// viewing my account
                                            System.out.println("Viewing your account(s)!");
                                            List<Account> myAccounts = dao.getCustomerAccount(customer.getId());
                                            myAccounts.forEach(System.out::println);
                                        }
                                        case 2 -> {// make new account
                                            System.out.println("Making a new account.");
                                            System.out.print("Please enter an account type you wish to make: ");
                                            Account_Type type = getAccountType();
                                            dao.makeAccount(customer, type);
                                        }
                                        case 3 -> {//deposit
                                            System.out.print("Enter the account number you want to deposit to: ");
                                            int id = getNumber();
                                            System.out.print("Enter the amount you want to deposit: ");
                                            int depositAmount = getNumber();
                                            dao.deposit(customer, id, depositAmount, false);
                                        }
                                        case 4 -> {// withdraw money
                                            System.out.print("Enter the account number you want to withdraw to: ");
                                            int id = getNumber();
                                            System.out.print("Enter the amount you want to withdraw: ");
                                            int withdrawAmount = getNumber();
                                            dao.withdraw(customer, id, withdrawAmount, false);
                                        }
                                        case 5 -> {// transfer money
                                            System.out.print("Enter the account you like to send/request money from: ");
                                            int account1 = getNumber();
                                            System.out.print("Enter the account that will be part of this transaction: ");
                                            int account2 = getNumber();
                                            System.out.print("How much money is involved: ");
                                            int amount = getNumber();
                                            System.out.print("Are you sending or receiving money: ");
                                            Money_Transfer moneyTransfer = getTransferType();
                                            dao.transferMoney(customer, account1, account2, amount, moneyTransfer);
                                        }
                                        case 6 -> {//view money transfer you are involved in
                                            System.out.println("Viewing money transfer list!");
                                            System.out.print("Which account do you want to see pending money transfer for: ");
                                            int account = getNumber();
                                            List<MoneyTransfer> moneyTransferList = dao.viewMoneyTransferList(customer, account);
                                            if(moneyTransferList.size() != 0) moneyTransferList.forEach(System.out::println);
                                            else System.out.println("There is no money transfer associated with this account or this account does not belong to you!");
                                        }
                                        case 7 -> {//approve money transfer
                                            System.out.print("Please enter the money transfer ID: ");
                                            int id = getNumber();
                                            System.out.print("Are you approving this transfer: " );
                                            boolean accept = getBoolean();
                                            dao.approveTransfer(customer, id, accept);
                                        }
                                        case 8 -> {
                                            customerLogin = false;
                                            System.out.println("Logging out of employee");
                                        }
                                        default -> System.out.println("Please choose from the following!!!");

                                    }
                                }
                            }
                            case 2 -> {
                                System.out.println("Employee Login!");
                                boolean employeeLogin = false;
                                User employee;
                                System.out.print("Please enter employee id: ");
                                int employeeId = getNumber();
                                System.out.print("Please enter the password: ");
                                String password = scanner.next();
                                employee = dao.getUser(employeeId, password, true);
                                if(employee != null){
                                    System.out.println("Login in as " + employee.getName() +"!");
                                    employeeLogin = true;
                                }
                                else System.out.println("Invalid login returning to previous screen!");

                                while(employeeLogin) {
                                    System.out.println("\nChoose 1 of the following!");
                                    System.out.println("1)View account pending list");
                                    System.out.println("2)List all customer");
                                    System.out.println("3)List all account");
                                    System.out.println("4)List all account own by a customer");
                                    System.out.println("5)View transaction");
                                    System.out.println("6)Approving new account");
                                    System.out.println("7)Resetting daily limit");
                                    System.out.println("8)Delete transaction history");
                                    System.out.println("9)Log out");

                                    input = getNumber();
                                    switch(input) {
                                        case 1 ->{// view pending account list
                                            System.out.println("Listing accounts need for approval!");
                                            List<Account> pendingAccounts = dao.viewPending();
                                            if(pendingAccounts.size() != 0) pendingAccounts.forEach(System.out::println);
                                            else System.out.println("There is no pending accounts!");
                                        }
                                        case 2 -> {// list all customer
                                            System.out.println("Listing all customer!");
                                            List<Customer> customers = dao.getCustomer();
                                            if(customers.size() != 0) customers.forEach(System.out::println);
                                            else System.out.println("There is no customer");
                                        }
                                        case 3 -> {// get Customer by id
                                            System.out.println("Listing all accounts!");
                                            List<Account> accounts = dao.getAccount();
                                            if(accounts.size() != 0) accounts.forEach(System.out::println);
                                            else System.out.println("No accounts found");
                                        }
                                        case 4 -> {// list all account of a customer
                                            System.out.print("Which customer's accounts do you want to list: ");
                                            List<Account> accounts = dao.getCustomerAccount(getNumber());
                                            if(accounts.size() != 0) accounts.forEach(System.out::println);
                                            else System.out.println("This customer does not have an account");
                                        }
                                        case 5 -> {// view transaction
                                            System.out.println("Viewing transaction list!");
                                            List<Transaction> transactions = dao.viewTransaction();
                                            if(transactions.size() != 0) transactions.forEach(System.out::println);
                                            else System.out.println("There is no transaction!");
                                        }
                                        case 6 -> {// approve new account
                                            Account account = new Account();
                                            System.out.print("Enter account number you trying to approve/deny: ");
                                            int id = getNumber();
                                            System.out.print("Are you approving(true) or denying(false) the account: ");
                                            boolean approve = getBoolean();
                                            account.setAccountNumber(id);
                                            account.setStatus(approve);
                                            dao.accountApproval(account);
                                        }
                                        case 7 -> {//Resetting daily limit
                                            System.out.println("Resetting daily limit");
                                            dao.clearList("Withdrawal");
                                        }
                                        case 8 -> {//Deleting transaction history
                                            System.out.println("Deleting transaction history!");
                                            dao.clearList("Transaction");
                                        }
                                        case 9 -> {
                                            employeeLogin = false;
                                            System.out.println("Logging out of employee");
                                        }
                                        default -> System.out.println("Please choose from the following!!!");
                                    }
                                }
                            }
                            case 3 -> {
                                login = false;
                                System.out.println("Exiting login screen");
                            }
                            default -> System.out.println("Please enter choose 1 of the following");
                        }
                    }
                }
                case 2 -> {//having problem with scanner next line
                    System.out.println("Welcome new Customer!");
                    System.out.print("Please enter your name: ");
                    Scanner sScanner = new Scanner(System.in);
                    String name = sScanner.next();
                    System.out.print("Please enter an email: ");
                    String email = scanner.next();
                    System.out.print("Please enter a password for your account: ");
                    String password = scanner.next();
                    System.out.print("Please enter an account type: ");
                    Account_Type type = getAccountType();
                    System.out.println(type);
                    dao.newAccount(name, email, password, type);
                }
                case 3 -> {
                    System.out.println("Database will now be reset to default!");
                    dao.databaseReset();
                }
                case 4 -> {
                    run = false;
                    ConnectionFactory.getConnection().close();
                    System.out.println("Good bye! See you next time!");
                }
                default -> System.out.println("Please choose 1 of the following");
            }
        }
    }

    public static int getNumber(){
        int num;
        Scanner scanner = new Scanner(System.in);
        try{
            num = scanner.nextInt();
        } catch(InputMismatchException ignore){
            num = 0;
        }
        return num;
    }
    public static boolean getBoolean(){
        boolean falseInput = true;
        boolean bool = false;
        do {
            try{
                Scanner scanner = new Scanner(System.in);
                boolean newBool = scanner.nextBoolean();
                falseInput = false;
                bool = newBool;
            }catch(InputMismatchException e) {
                System.out.println("Enter a boolean");
            }
        } while(falseInput);
        return bool;
    }
    public static Account_Type getAccountType(){
        do{
            try{
                Scanner scanner = new Scanner(System.in);
                return Account_Type.valueOf(scanner.next());
            } catch(Exception ignore){
                System.out.print("Please enter either 'checking' or 'saving': ");
            }
        } while(true);
    }
    public static Money_Transfer getTransferType(){
        do{
            try{
                Scanner scanner = new Scanner(System.in);
                return Money_Transfer.valueOf(scanner.next());
            } catch(Exception ignore){
                System.out.print("Please enter either 'send' or 'request': ");
            }
        } while(true);
    }
}
