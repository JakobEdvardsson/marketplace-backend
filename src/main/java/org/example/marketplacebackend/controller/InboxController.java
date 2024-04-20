package org.example.marketplacebackend.controller;

import org.aspectj.bridge.Message;
import org.example.marketplacebackend.DTO.incoming.UserDTO;
import org.example.marketplacebackend.DTO.outgoing.MessageCreatedResponseDTO;
import org.example.marketplacebackend.DTO.outgoing.UserRegisteredResponseDTO;
import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.model.Inbox;
import org.example.marketplacebackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("v1/inbox")
@CrossOrigin(origins = "localhost:3000", allowCredentials = "true")
@RestController
public class InboxController {

  public InboxController(UserService userService) {
    //TODO: ASK IF NEEDED
  }

  //TODO: POST, DELETE, GET

  @PostMapping("") //TODO: ASK ABOUT LINK FOR INBOX
  public ResponseEntity<?> sendMessage(@RequestBody Account user, String message) {
    Inbox inbox = new Inbox();

    inbox.setAccount(user);
    inbox.setMessage(message);
    inbox.setIsRead(false);

    MessageCreatedResponseDTO messageDTO = new MessageCreatedResponseDTO(user, message, false);

    return ResponseEntity.status(HttpStatus.CREATED).body(messageDTO);

  }

  @DeleteMapping("")
  public ResponseEntity<?> deleteMessage() {
    String msg = inbox.getMessage();
    this.inbox.remove(msg);

  }

  //TODO: ASK HOW TO SEND ID
  @GetMapping("")
  public ResponseEntity<?> getMessage(@RequestBody Account user, UUID messageID) {



    return ResponseEntity.status(HttpStatus.OK).body(messageDTO);

  }


}
