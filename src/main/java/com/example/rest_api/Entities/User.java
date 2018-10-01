package com.example.rest_api.Entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table
public class User {

    @Id
    public String username;
    public String password;

    @OneToMany(mappedBy = "Transactions.user")
    List<String> transaction_id;

    User(){

    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
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

    public List<String> getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(List<String> transaction_id) {
        this.transaction_id = transaction_id;
    }
}
