package com.example.controller;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.repository.AccountRepository;
import com.example.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */

@RestController
@RequestMapping
public class SocialMediaController {
    private final AccountRepository accountRepository;
    private final MessageRepository messageRepository;

    @Autowired
    public SocialMediaController(AccountRepository accountRepository,
                                 MessageRepository messageRepository) {
        this.accountRepository = accountRepository;
        this.messageRepository = messageRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<Account> register(@RequestBody Account newAccount) {
        if (newAccount.getUsername() == null || newAccount.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        if (newAccount.getPassword() == null || newAccount.getPassword().length() < 4) {
            return ResponseEntity.badRequest().build();
        }
        
        if (accountRepository.findByUsername(newAccount.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        Account savedAccount = accountRepository.save(newAccount);
        
        return ResponseEntity.ok(savedAccount);
    }

    @PostMapping("/login")
    public ResponseEntity<Account> login(@RequestBody Account loginRequest) {
        if (loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        
        return accountRepository
                .findByUsername(loginRequest.getUsername())
                .map(foundAccount -> {
                    if (foundAccount.getPassword().equals(loginRequest.getPassword())) {
                        return ResponseEntity.ok(foundAccount);
                    } else {
                        return new ResponseEntity<Account>(HttpStatus.UNAUTHORIZED);
                    }
                })
                .orElseGet(() -> {
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                });
    }

    @PostMapping("/messages")
    public ResponseEntity<Message> createNewMessage(@RequestBody Message newMessage) {
        if (newMessage.getMessageText() == null
                || newMessage.getMessageText().trim().isEmpty()
                || newMessage.getMessageText().length() > 255) {
            return ResponseEntity.badRequest().build();  // 400
        }

        Integer postedBy = newMessage.getPostedBy();
        if (postedBy == null || !accountRepository.findById(postedBy).isPresent()) {
            return ResponseEntity.badRequest().build();  // 400
        }

        Message savedMessage = messageRepository.save(newMessage);

        return ResponseEntity.ok(savedMessage);
    }

    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages() {
        List<Message> messages = messageRepository.findAll();

        return ResponseEntity.ok(messages);
    }

    @GetMapping("/messages/{message_id}")
    public ResponseEntity<Message> getOneMessage(@PathVariable("message_id") Integer messageId) {
        Optional<Message> optionalMessage = messageRepository.findById(messageId);

        return optionalMessage
                .map(ResponseEntity::ok)         
                .orElseGet(() -> ResponseEntity.ok().build()); 
    }

    @DeleteMapping("/messages/{message_id}")
    public ResponseEntity<?> deleteMessage(@PathVariable("message_id") Integer messageId) {
        Optional<Message> optionalMessage = messageRepository.findById(messageId);

        if (optionalMessage.isPresent()) {
            messageRepository.delete(optionalMessage.get());

            return ResponseEntity.ok(1);
        } else {
            return ResponseEntity.ok().build();
        }
    }

    @PatchMapping("/messages/{message_id}")
    public ResponseEntity<Integer> updateMessage(@PathVariable("message_id") Integer messageId,
                                                 @RequestBody Map<String, Object> updates) {
        try {
            Optional<Message> optionalMessage = messageRepository.findById(messageId);
            if (optionalMessage.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Message existingMessage = optionalMessage.get();

            Object newTextObj = updates.get("messageText");
            if (newTextObj == null) {
                return ResponseEntity.badRequest().build();
            }

            String newText = newTextObj.toString();

            if (newText.trim().isEmpty() || newText.length() > 255) {
                return ResponseEntity.badRequest().build();
            }

            existingMessage.setMessageText(newText);
            messageRepository.save(existingMessage);

            return ResponseEntity.ok(1);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
