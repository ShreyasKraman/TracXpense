package com.example.rest_api.Controller;

import com.example.rest_api.Service.ResponseService;
import com.example.rest_api.Service.UserService;
import com.example.rest_api.Service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value="/")
public class AuthorizationController {

    @Autowired
    UserService userService;

    @Autowired
    ValidationService validateService;

    @Autowired
    ResponseService responseService;

    @RequestMapping(value="/", method = RequestMethod.GET)
    public Map<String,Object> authAndLogin(@RequestHeader(value="Authorization", defaultValue = "NoValueFound")String auth){

        if(auth.isEmpty() || auth.equals("NoValueFound")){
            return responseService.generateResponse(HttpStatus.UNAUTHORIZED,"You are not logged in");
        }

        return userService.authUser(auth);

    }

    @RequestMapping(value="/user/register",method=RequestMethod.POST)
    public Map<String,Object> register(@RequestHeader(value="Authorization")String auth){

        if(auth.isEmpty() || auth.equals("NoValueFound")){
            return responseService.generateResponse(HttpStatus.UNAUTHORIZED,"Please enter username and password");
        }

        return userService.createUser(auth);

    }

}
