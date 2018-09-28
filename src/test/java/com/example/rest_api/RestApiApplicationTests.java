package com.example.rest_api;

import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.constraints.AssertTrue;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RestApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestApiApplicationTests {

    @LocalServerPort
    private int port;

    TestRestTemplate testRest = new TestRestTemplate();

    @Test
    public void testLoginSuccess() throws JSONException{

        HttpHeaders header = createHeaders("abcd@gmail.com","abcd1234");

        HttpEntity<String> entity = new HttpEntity<String>(null,header);

        ResponseEntity<String> response = testRest.exchange(
                            createUrlWithPort("/"),
                            HttpMethod.GET,entity,String.class);

        DateFormat df = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        String expectedString = "{\"Data\":\"Current Time: "+df.format(date)+"\",\"Response Code\":\"OK\"}";

        JSONAssert.assertEquals(expectedString, response.getBody(),false);

    }

    @Test
    public void testLoginFailed() throws JSONException{
        HttpHeaders header = createHeaders("noaccountexits@gmail.com","abcd1234");

        HttpEntity<String> entity = new HttpEntity<String>(null,header);

        ResponseEntity<String> response = testRest.exchange(
                createUrlWithPort("/"),
                HttpMethod.GET,entity,String.class);

        String expectedString = "{\"Data\":\"You are not logged in\",\"Response Code\":\"NOT_FOUND\"}";

        JSONAssert.assertEquals(expectedString, response.getBody(),false);
    }

    public HttpHeaders createHeaders(String username, String password){
        return new HttpHeaders(){{
            String auth = username + ":" + password;
            byte byteCode[] = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
            String authHeader = "Basic " + new String(byteCode);
            set("Authorization",authHeader);
        }};
    }

    public String createUrlWithPort(String uri){
        return "http://localhost:"+port+uri;
    }

}
