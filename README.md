# Project0 #

The Bank app is a console-based application that simulates banking operations. A customer can apply for an account, view their balance, and make withdrawals and deposits. An employee can approve or deny accounts and view account balances for their customers.

Requirements

Functionality should reflect the below user stories.
Data is stored in a database.
A custom stored procedure is called to perform some portion of the functionality.
Data Access is performed through the use of JDBC in a data layer consisting of Data Access Objects.
All input is received using the java.util.Scanner class.

- As a user:
  - Login
  - Make new customer account
- As a Customer:
  - View your accounts
  - Make a new bank account
  - Deposit money
  - Withdraw money
  - Request/send money(money transfer)
  - View pending money transfer
  - Accept/decline money transfer request, if you are second party
- As an Employee:
  - View pending account list
  - List all customer
  - List all account
  - List all account that belongs to a certain customer
  - View transaction history
  - Approve new accounts
  - Reset daily withdraw limit for 'Saving accounts'
  - Delete transaction history
