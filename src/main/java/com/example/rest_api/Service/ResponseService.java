package com.example.rest_api.Service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ResponseService {

    public Map<String,Object> generateResponse(HttpStatus status, Object reason){

        Map<String,Object> response = new HashMap<String,Object>();
        response.put("Response Code",status);
        response.put("Data",reason);
        return response;

    }

}
