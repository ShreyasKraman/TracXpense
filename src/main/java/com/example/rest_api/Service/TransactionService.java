package com.example.rest_api.Service;

import com.example.rest_api.Dao.TransactionsDao;
import com.example.rest_api.Entities.Transactions;
import com.example.rest_api.Entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {

    @Autowired
    TransactionsDao transactionDao;

    @Autowired
    ResponseService responseService;

    @Autowired
    UserService userService;

    public Map<String,Object> getTransactions(String auth){

        String[] userCredentials = userService.getUserCredentials(auth);

        List<Transactions> transactions = new ArrayList<Transactions>();

        if(userCredentials.length != 0){

            if(userService.authUser(userCredentials)){

                List<String> transactionIds = userService.getTransactionsIds(userCredentials);

                Iterator it = transactionIds.iterator();

                while(it.hasNext()){
                    String transactionId = (String)it.next();
                    Transactions transact = (Transactions) transactionDao.getOne(transactionId);
                    transactions.add(transact);
                }
                return responseService.generateResponse(HttpStatus.OK,transactions);

            }

        }
        return responseService.generateResponse(HttpStatus.UNAUTHORIZED,"Unauthorized");
    }

    public Map<String,Object> createTransaction(String auth, Transactions transaction){

        String[] userCredentials = userService.getUserCredentials(auth);

        return null;

    }


}
