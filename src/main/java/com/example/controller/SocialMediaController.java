package com.example.controller;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */

@RestController
@RequestMapping
public class SocialMediaController {
    private final AccountService accountService;
    private final MessageService messageService;

    @Autowired
    public SocialMediaController(AccountService accountService,
                                 MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
    }

    @PostMapping("/register")
    public ResponseEntity<Account> register(@RequestBody Account newAccount) {
        try {
            Account created = accountService.createAccount(newAccount);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Account> login(@RequestBody Account loginRequest) {
        if (loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
            return ResponseEntity.status(401).build();
        }
        Optional<Account> maybeAccount = accountService.findByUsername(loginRequest.getUsername());
        if (maybeAccount.isPresent() && maybeAccount.get().getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.ok(maybeAccount.get());
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/messages")
    public ResponseEntity<Message> createMessage(@RequestBody Message newMessage) {
        try {
            Message created = messageService.createMessage(newMessage);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages() {
        List<Message> messages = messageService.getAllMessages();
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/messages/{message_id}")
    public ResponseEntity<Message> getOneMessage(@PathVariable("message_id") Integer messageId) {
        Optional<Message> maybeMessage = messageService.getMessageById(messageId);
        return maybeMessage
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.ok().build());
    }

    @PatchMapping("/messages/{message_id}")
    public ResponseEntity<Integer> updateMessage(
            @PathVariable("message_id") Integer messageId,
            @RequestBody Message requestBody) {
        try {
            // Combine path ID and request body to form the updated message
            requestBody.setMessageId(messageId);
            messageService.updateMessage(requestBody);
            return ResponseEntity.ok(1);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/messages/{message_id}")
    public ResponseEntity<Integer> deleteMessage(@PathVariable("message_id") Integer messageId) {
        boolean deleted = messageService.deleteMessageById(messageId);
        if (deleted) {
            return ResponseEntity.ok(1);
        } else {
            return ResponseEntity.ok().build(); 
        }
    }

    @GetMapping("/accounts/{account_id}/messages")
    public ResponseEntity<List<Message>> getAllMessagesFromUser(@PathVariable("account_id") Integer accountId) {
        List<Message> userMessages = messageService.getAllMessagesByAccountId(accountId);
        return ResponseEntity.ok(userMessages);
    }
}
