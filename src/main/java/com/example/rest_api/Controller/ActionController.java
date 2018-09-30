package com.example.rest_api.Controller;

import com.example.rest_api.Service.UserService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ActionController {

    @Autowired
    UserService userService;

    @RequestMapping(value="/", method = RequestMethod.GET)
    public Map<String,Object> authAndLogin(@RequestHeader(value="Authorization", defaultValue = "NoValueFound")String auth){

        if(auth.isEmpty() || auth == null || auth.equals("NoValueFound")){
            return generateResponse(HttpStatus.NOT_FOUND,"You are not logged in");
        }

        String []userCredentials = getUserCredentials(auth);

        if(!validateUsername(userCredentials[0])){
            return generateResponse(HttpStatus.BAD_GATEWAY,"Invalid Email Address");
        }

        if(userService.authUser(userCredentials)){

            DateFormat df = new SimpleDateFormat("HH:mm");
            Date date = new Date();
            return generateResponse(HttpStatus.OK,"Current Time: "+df.format(date));
        }else{
            return generateResponse(HttpStatus.NOT_FOUND,"No account found. Please Register");
        }

    }

    @RequestMapping(value="/register",method=RequestMethod.POST)
    public Map<String,Object> register(@RequestHeader(value="Authorization")String auth){

        String []userCredentials = getUserCredentials(auth);

        if(!validateUsername(userCredentials[0])){
            return generateResponse(HttpStatus.BAD_REQUEST,"Error:Invalid Email Address");
        }

        if(userService.authUser(userCredentials)){
            return generateResponse(HttpStatus.BAD_REQUEST,"Account already exists");
        }else{
            if(userService.createUser(userCredentials)){
                return generateResponse(HttpStatus.OK,"Account created successfully");
            }
        }

        return generateResponse(HttpStatus.BAD_REQUEST,"Error");
    }

    private boolean validateUsername(String username){
        String regex = "[A-Za-z1-9]+@[A-Za-z0-9]+\\.[A-Za-z]{2,}";
        return username.matches(regex);
    }

    private String[] getUserCredentials(String auth){
        //Authorization: Basic (Base64)Encoded
        String []authParts = auth.split(" ");

        byte[] decode = Base64.decodeBase64(authParts[1]);

        return new String(decode).split(":");
    }

    private Map<String,Object> generateResponse(HttpStatus status,String reason){

        Map<String,Object> response = new HashMap<String,Object>();
        response.put("Response Code",status);
        response.put("Data",reason);
        return response;

    }
}
