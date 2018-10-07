package com.example.rest_api.Service;

import com.example.rest_api.Dao.AttachmentDao;
import com.example.rest_api.Dao.TransactionsDao;
import com.example.rest_api.Dao.UserDao;
import com.example.rest_api.Entities.Attachments;
import com.example.rest_api.Entities.Transactions;
import com.example.rest_api.Entities.User;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class AttachmentService {

    @Autowired
    UserService userService;

    @Autowired
    UserDao userDao;

    @Autowired
    TransactionService transactionService;

    @Autowired
    TransactionsDao transactionsDao;

    @Autowired
    AttachmentDao attachmentDao;

    @Autowired
    ResponseService responseService;

    public List<Attachments> getAllAttachments(String auth, String transcation_id){

        String userCredentials[] = userService.getUserCredentials(auth);

        Optional<User> optionalUser =  userDao.findById(userCredentials[0]);
        try {

            User user = optionalUser.get();
            if (userService.authUser(userCredentials)) {
                if (transactionService.ifTransactionAttachedToUser(user, transcation_id) != null) {

                    Transactions transactions = transactionService.ifTransactionAttachedToUser(user,transcation_id);

                    return transactions.getAttachmentsList();
                }
            }
        }catch(Exception e){}
        return null;

    }

    public ResponseEntity addAttachment(String auth, String transcation_id, Attachments attachment){
        String userCredentials[] = userService.getUserCredentials(auth);

        Optional<User> optionalUser =  userDao.findById(userCredentials[0]);
        try {

            User user = optionalUser.get();
            if (userService.authUser(userCredentials)) {
                if (transactionService.ifTransactionAttachedToUser(user, transcation_id) != null) {

                    Transactions transaction = transactionService.ifTransactionAttachedToUser(user, transcation_id);
                    File file = new File(attachment.getUrl());
                    String extension = FilenameUtils.getExtension(file.getName());
                    if(!extension.equals("jpeg") && !extension.equals("jpg") && !extension.equals("png")){
                        System.out.print(extension);
                        return responseService.generateResponse(HttpStatus.UNAUTHORIZED,
                                "{\"Response\":\"Enter file with jpeg, jpg or png extension only\"}");
                    }
                    String newPath = "C:\\Users\\shrey\\Documents\\Shreyas\\AWS Cloud\\Cloud_Attachments\\"+file.getName();
                    if(!file.renameTo(new File(newPath))){
                        return null;
                    }

                    String nameOfTheFile = file.getName();
                    Random rand = new Random();
                    int randId = rand.nextInt(10000);
                    String id = nameOfTheFile.substring(0,nameOfTheFile.length() - 4) + String.valueOf(randId);
                    while (true) {
                        String hashedId = userService.hash(id);
                        if (!ifAttachmentExists(hashedId)) {
                            if (hashedId.contains("/")) {
                                hashedId = hashedId.replace("/", "-");
                            }
                            if (hashedId.contains(".")) {
                                hashedId = hashedId.replace(".", "-");
                            }
                            attachment.setId(hashedId);
                            attachment.setUrl(newPath);
                            break;
                        }
                    }
                    if (!attachment.getId().isEmpty()) {
                        transaction.addAttachment(attachment);
                        attachment.setTransactions(transaction);
                        attachmentDao.save(attachment);
                        return responseService.generateResponse(HttpStatus.OK,attachment);
                    }
                }
            }
        }catch(Exception e){System.out.println(e.getMessage());}

        return responseService.generateResponse(HttpStatus.UNAUTHORIZED,null);
    }

    public boolean deleteAttachment(String auth, String transactionId, String attachmentId){
        String userCredentials[] = userService.getUserCredentials(auth);

        Optional<User> optionalUser =  userDao.findById(userCredentials[0]);
        try {

            User user = optionalUser.get();
            if (userService.authUser(userCredentials)) {
                if (transactionService.ifTransactionAttachedToUser(user, transactionId) != null) {
                    Transactions transactions = transactionService.ifTransactionAttachedToUser(user,transactionId);
                    if(transactions!=null){
                        List<Attachments> attachmentList = transactions.getAttachmentsList();
                        Iterator it = attachmentList.iterator();
                        while(it.hasNext()){
                            Attachments attachments = (Attachments)it.next();
                            if(attachments.getId().equals(attachmentId))
                                attachmentDao.delete(attachments);
                                return transactions.deleteAttachment(attachments);
                        }
                    }

                }
            }
        }catch(Exception e){

        }

        return false;
    }


    public boolean ifAttachmentExists(String id){
        Optional<Attachments> optionalAttachments = attachmentDao.findById(id);
        try{
            Attachments attachments = optionalAttachments.get();
            if(attachments != null){
                return true;
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }


}
