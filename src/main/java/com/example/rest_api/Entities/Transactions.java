package com.example.rest_api.Entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table
public class Transactions {

    @Id
    private String transaction_id;

    @OneToOne
    private User user;

    private String description;
    private String merchant;
    private String amount;
    private String date;
    private String category;

    public Transactions() {
    }

    public Transactions(String transaction_id, User user, String description, String merchant, String amount, String date, String category) {
        this.transaction_id = transaction_id;
        this.user = user;
        this.description = description;
        this.merchant = merchant;
        this.amount = amount;
        this.date = date;
        this.category = category;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
