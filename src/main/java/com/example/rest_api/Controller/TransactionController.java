package com.example.rest_api.Controller;

import com.example.rest_api.Dao.TransactionsDao;
import com.example.rest_api.Entities.Transactions;
import com.example.rest_api.Service.ResponseService;
import com.example.rest_api.Service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
//@RequestMapping(value = "/transact")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @Autowired
    ResponseService responseService;

    @RequestMapping(value="/transact/", method = RequestMethod.GET)
    public ResponseEntity getTransaction(@RequestHeader(value="Authorization", defaultValue = "No Auth")String auth){

        if(auth.isEmpty() || auth.equals("NoValueFound")){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        if(transactionService.getTransactions(auth) == null){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(transactionService.getTransactions(auth));

    }

    @RequestMapping(value="/transact/create", method = RequestMethod.POST)
    public ResponseEntity createTransaction(@RequestHeader(value="Authorization",
            defaultValue = "No Auth")String auth, @RequestBody Transactions transaction){

        if(auth.isEmpty() || auth.equals("NoValueFound")){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        if(transaction == null){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        if(transactionService.createTransaction(auth,transaction)){
            return ResponseEntity.status(HttpStatus.CREATED)
                        .body(transaction);
        }

        return new ResponseEntity(HttpStatus.UNAUTHORIZED);

    }

}
