package com.example.rest_api.Controller;

import com.example.rest_api.Entities.Attachments;
import com.example.rest_api.Service.AttachmentService;
import com.example.rest_api.Service.ResponseService;
import com.example.rest_api.Service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AttachmentController {

    @Autowired
    AttachmentService attachmentService;

    @GetMapping("/transact/{transaction_id}/attachments")
    public ResponseEntity getAllAttachments(@RequestHeader(value="Authorization", defaultValue = "NoAuth")String Auth,
                                            @PathVariable(value = "transaction_id")String transactionId){

        if(!Auth.equals("NoAuth") && !transactionId.isEmpty()){
            List<Attachments> attachmentsList = attachmentService.getAllAttachments(Auth,transactionId);

                return ResponseEntity.status(HttpStatus.OK)
                        .body(attachmentsList);

        }

        return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/transact/{transaction_id}/attachments")
    public ResponseEntity addAttachment(@RequestHeader(value="Authorization",defaultValue = "NoAuth")String auth,
                                        @PathVariable(value="transaction_id")String transactionId,
                                        @RequestBody Attachments attachments){

        if(!auth.equals("NoAuth") && !transactionId.isEmpty()){
           return attachmentService.addAttachment(auth,transactionId,attachments);
        }

        return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    @DeleteMapping("/transact/{transaction_id}/attachments/{attachment_id}")
    public ResponseEntity deleteAttachment(@RequestHeader(value="Authorization",defaultValue = "NoAuth")String auth,
                                           @PathVariable(value="transaction_id")String transactionId,
                                           @PathVariable(value="attachment_id")String attachmentId){

        if(!auth.equals("NoAuth") && !transactionId.isEmpty() && !attachmentId.isEmpty()){
            if(attachmentService.deleteAttachment(auth,transactionId,attachmentId)){
                return new ResponseEntity(HttpStatus.NO_CONTENT);
            }
        }

        return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

}
