package com.revature;

public class Customer extends User{

    public Customer() {
    }

    public Customer(int id, String name, String email, String password) {
        super(id, name, email, password);
    }

    @Override
    public String toString() {
        return "Customer: " +
                "id = " + super.getId() +
                "\tname = " + super.getName() +
                "\temail = " + super.getEmail();
    }
}
