package com.example.controller;

import com.example.entity.Account;
import com.example.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    public SocialMediaController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
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
}
