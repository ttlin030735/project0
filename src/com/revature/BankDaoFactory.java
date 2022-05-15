package com.revature;

public class BankDaoFactory {
    public static BankDao dao;

    private BankDaoFactory(){}

    public static BankDao getBankDao(){
        if(dao == null) dao = new BankDaoImpl();
        return dao;
    }
}
