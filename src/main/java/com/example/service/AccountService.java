package com.example.service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(Account newAccount) {
        if (newAccount.getUsername() == null || newAccount.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be blank");
        }
        if (newAccount.getPassword() == null || newAccount.getPassword().length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters");
        }

        Optional<Account> existingAccount = accountRepository.findByUsername(newAccount.getUsername());
        if (existingAccount.isPresent()) {
            throw new IllegalStateException("Username already exists");
        }

        return accountRepository.save(newAccount);
    }

    public Optional<Account> findByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

}
