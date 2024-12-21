package com.example.service;

import com.example.entity.Message;
import com.example.repository.AccountRepository;
import com.example.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository, 
                          AccountRepository accountRepository) {
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
    }

    public Message createMessage(Message newMessage) {
        if (newMessage.getMessageText() == null || newMessage.getMessageText().trim().isEmpty()) {
            throw new IllegalArgumentException("Message text cannot be blank");
        }
        if (newMessage.getMessageText().length() > 255) {
            throw new IllegalArgumentException("Message text cannot exceed 255 characters");
        }

        if (newMessage.getPostedBy() == null || !accountRepository.existsById(newMessage.getPostedBy())) {
            throw new IllegalArgumentException("Invalid or non-existent user (postedBy)");
        }

        return messageRepository.save(newMessage);
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Optional<Message> getMessageById(Integer messageId) {
        return messageRepository.findById(messageId);
    }

    public Message updateMessage(Message updatedMessage) {
        Optional<Message> existingOptional = messageRepository.findById(updatedMessage.getMessageId());
        if (existingOptional.isEmpty()) {
            throw new IllegalArgumentException("Cannot update: message does not exist");
        }

        if (updatedMessage.getMessageText() == null 
                || updatedMessage.getMessageText().trim().isEmpty()
                || updatedMessage.getMessageText().length() > 255) {
            throw new IllegalArgumentException("Invalid message text");
        }

        Message existingMessage = existingOptional.get();
        existingMessage.setMessageText(updatedMessage.getMessageText());
        
        return messageRepository.save(existingMessage);
    }

    public boolean deleteMessageById(Integer messageId) {
        Optional<Message> existingMessage = messageRepository.findById(messageId);
        if (existingMessage.isPresent()) {
            messageRepository.delete(existingMessage.get());
            return true;
        }
        return false;
    }

    public List<Message> getAllMessagesByAccountId(Integer accountId) {
        return messageRepository.findAllByPostedBy(accountId);
    }
}
