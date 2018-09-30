package com.example.rest_api.Dao;

import com.example.rest_api.Entities.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionsDao extends JpaRepository<Transactions,String> {

}
