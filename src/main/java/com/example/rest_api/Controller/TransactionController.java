package com.example.rest_api.Controller;

import com.example.rest_api.Dao.TransactionsDao;
import com.example.rest_api.Entities.Transactions;
import com.example.rest_api.Service.ResponseService;
import com.example.rest_api.Service.TransactionService;
import com.example.rest_api.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.transaction.TransactionScoped;
import java.util.Map;

@RestController
@RequestMapping(value = "/transact")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @Autowired
    ResponseService responseService;

    @RequestMapping(value="/transact/", method = RequestMethod.GET)
    public Map<String,Object> getTransaction(@RequestHeader(value="Authorization", defaultValue = "No Auth")String auth){

        if(auth.isEmpty() || auth.equals("NoValueFound")){
            return responseService.generateResponse(HttpStatus.UNAUTHORIZED,"You are not logged in");
        }

        return transactionService.getTransactions(auth);

    }

    @RequestMapping(value="/transact/create", method = RequestMethod.POST)
    public Map<String,Object> createTransaction(@RequestHeader(value="Authorization",
            defaultValue = "No Auth")String auth, @RequestBody Transactions transaction){

        if(auth.isEmpty() || auth.equals("NoValueFound")){
            return responseService.generateResponse(HttpStatus.UNAUTHORIZED,"You are not logged in");
        }

        if(transaction == null){
            return responseService.generateResponse(HttpStatus.BAD_REQUEST,"No transaction passed for creation");
        }

        

    }

}
