package com.example.rest_api.Entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
public class User {

    @Id
    public String username;
    public String password;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
    public List<Transactions> transactions;


    User(){
        transactions = new ArrayList<>();
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        transactions = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Transactions> getTransactions() {
        return transactions;
    }

    public Transactions addTransaction(Transactions transactionValue){
        try{
            transactions.add(transactionValue);
            return transactionValue;
        }catch (Exception e){
            return null;
        }
    }
}
