package com.example.rest_api.Service;

import com.example.rest_api.Dao.UserDao;
import com.example.rest_api.Entities.User;
import org.apache.tomcat.util.codec.binary.Base64;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service

public class UserService {

    @Autowired
    UserDao userDao;

    @Autowired
    ResponseService responseService;

    @Autowired
    ValidationService validateService;

    public Map<String,Object> authUser(String auth){

        String []userCredentials = getUserCredentials(auth);

        if(userCredentials.length == 0){
            return responseService.generateResponse(HttpStatus.UNAUTHORIZED, "You are not logged in");
        }

        if(!validateService.validateUsername(userCredentials[0])){
            return responseService.generateResponse(HttpStatus.BAD_GATEWAY,"Invalid Email Address");
        }

        try {
            if(authUser(userCredentials)){
                DateFormat df = new SimpleDateFormat("HH:mm");
                Date date = new Date();
                return responseService.generateResponse(HttpStatus.OK,"Current Time: "+df.format(date));
            }
        }catch(NoSuchElementException e){
            return responseService.generateResponse(HttpStatus.UNAUTHORIZED,"No account found. Please register");
        }

        return responseService.generateResponse(HttpStatus.UNAUTHORIZED,"No account found. Please register");
    }

    public Map<String,Object> createUser(String auth){
        String []userCredentials = getUserCredentials(auth);

        if(userCredentials.length == 0){
            return responseService.generateResponse(HttpStatus.UNAUTHORIZED, "Please enter username and password");
        }

        if(!validateService.validateUsername(userCredentials[0])){
            return responseService.generateResponse(HttpStatus.BAD_REQUEST,"Error:Invalid Email Address");
        }



        if(authUser(userCredentials)){
            return responseService.generateResponse(HttpStatus.BAD_REQUEST,"Account already exists");
        }else{
            try {

                String username = userCredentials[0];
                String password = userCredentials[1];

                String hashedPassword = hash(password);

                User user = new User(username, hashedPassword);

                userDao.save(user);

                return responseService.generateResponse(HttpStatus.OK,"Account created successfully");
            }
            catch(Exception e){
                System.out.print(e.getMessage());
                return responseService.generateResponse(HttpStatus.FORBIDDEN,"Account creation failed");
            }
        }

    }

    public boolean updateUser(String []userCredentials){

        String username = userCredentials[0];

        String password = userCredentials[1];

        try{

            String hashedPassword = hash(password);
            
            User user = new User(username,password);
            User existing_user = userDao.getOne(username);

            existing_user.setPassword(hashedPassword);

            return true;
        }catch(Exception e){
            return false;
        }
    }

    public boolean deleteUser(String []userCredentials){

        try{

            String username = userCredentials[0];

            userDao.deleteById(username);

            return true;

        }catch(Exception e){
            return false;
        }

    }

    protected boolean authUser(String []userCredentials){

        String username = userCredentials[0];
        String password = userCredentials[1];

        Optional<User> optionalUser = userDao.findById(username);

        try{
            User user = optionalUser.get();
            return checkHash(password,user.password);

        }catch (Exception e){
            return false;
        }

    }


    protected String hash(String password){
        if(password.isEmpty() || password == null){
            return null;
        }

        //Creating random salt with limit of 34
        Random rand = new Random();
        int log_rounds = rand.nextInt(20);

        return BCrypt.hashpw(password,BCrypt.gensalt(log_rounds));
    }

    protected boolean checkHash(String password,String hash){
        if(password.isEmpty() || password == null){
            return false;
        }

        return BCrypt.checkpw(password,hash);
    }

    protected String[] getUserCredentials(String auth){
        //Authorization: Basic (Base64)Encoded
        String []authParts = auth.split(" ");

        byte[] decode = Base64.decodeBase64(authParts[1]);

        return new String(decode).split(":");
    }


}
