package com.example.rest_api.Service;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
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
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class S3AttachmentService {

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

    private String clientRegion = "us-east-1";

    //S3 Bucket name
    private String bucketName = "cyse6225-fall2018-kalyanramans.me.csye6225.com";

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

                    if(uploadToS3(attachment.getUrl()) == null){
                        return responseService.generateResponse(HttpStatus.UNAUTHORIZED,
                                "{\"Response\":\"failed to upload to s3\"}");
                    }

                    String newPath = uploadToS3(attachment.getUrl());
                    Attachments newAttachment = new Attachments();
                    newAttachment.setUrl(newPath);

                    if (!newAttachment.getId().isEmpty() && !newAttachment.getUrl().isEmpty()) {
                        transaction.addAttachment(newAttachment);
                        newAttachment.setTransactions(transaction);
                        attachmentDao.save(newAttachment);
                        transactionsDao.save(transaction);
                        return responseService.generateResponse(HttpStatus.OK,newAttachment);
                    }
                }
            }
        }catch(Exception e){System.out.println(e.getMessage());}

        return responseService.generateResponse(HttpStatus.UNAUTHORIZED,null);
    }

    public ResponseEntity updateAttachment(String auth, String transactionId,
                                           Attachments attachment, String attachmentId){
        String userCredentials[] = userService.getUserCredentials(auth);

        Optional<User> optionalUser =  userDao.findById(userCredentials[0]);
        try {

            User user = optionalUser.get();
            if (userService.authUser(userCredentials)) {
                if (transactionService.ifTransactionAttachedToUser(user, transactionId) != null) {
                    Transactions transactions = transactionService.ifTransactionAttachedToUser(user, transactionId);
                    if (transactions != null) {

                        Attachments previousAttachment = transactions.getPreviousAttachment(attachmentId);

                        if(previousAttachment != null) {
                            URL fileUrl = new URL(previousAttachment.getUrl());

                            String objectKeyName = FilenameUtils.getName(fileUrl.getPath());

                            if(updateInS3(attachment,objectKeyName) != null ){
                                String updatedUrl = updateInS3(attachment,objectKeyName);
                                attachment.setUrl(updatedUrl);
                                attachmentDao.save(attachment);
                                transactionsDao.save(transactions);
                            }
                        }



                    }
                }
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }


        return new ResponseEntity(HttpStatus.UNAUTHORIZED);
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
                        URL existingURL = null;
                        while(it.hasNext()){
                            Attachments attachments = (Attachments)it.next();
                            if(attachments.getId().equals(attachmentId))
                                existingURL = new URL(attachments.getUrl());
                                String objectKeyName = FilenameUtils.getName(existingURL.getPath());
                                if(objectKeyName != null) {
                                    if(!deleteInS3(objectKeyName)){
                                        break;
                                    }
                                    attachmentDao.delete(attachments);
                                }else{
                                    break;
                                }
                            return transactions.deleteAttachment(attachments);
                        }
                    }

                }
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
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

    public String uploadToS3(String fileUrl){

        String fileObjectKeyName = FilenameUtils.getName(fileUrl);

        String fileName = fileUrl;

        try{

            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(clientRegion)
                    .withCredentials(new DefaultAWSCredentialsProviderChain())
                    .build();

            PutObjectRequest request = new PutObjectRequest(bucketName, fileObjectKeyName, new File(fileName));
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/"+FilenameUtils.getExtension(fileUrl));
            metadata.addUserMetadata("x-amz-meta-title", "Your Profile Pic");
            request.setMetadata(metadata);
            s3Client.putObject(request);

            //Get url

            String bucketUrl = " https://s3.amazonaws.com/"+bucketName+"/"+fileObjectKeyName;

            return bucketUrl;
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return null;
    }

    public String updateInS3(Attachments attachments,String objectKeyName){

        if(deleteInS3(objectKeyName)){
            if(uploadToS3(attachments.getUrl())!=null){
                return uploadToS3(attachments.getUrl());
            }
        }

        return null;
    }

    public boolean deleteInS3(String objectKeyName){

        try {

            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(clientRegion)
                    .withCredentials(new DefaultAWSCredentialsProviderChain())
                    .build();

            s3Client.deleteObject(new DeleteObjectRequest(bucketName,objectKeyName));
            return true;
        }catch (Exception e){
            System.out.println("failed to delete");
        }


        return false;
    }

}
