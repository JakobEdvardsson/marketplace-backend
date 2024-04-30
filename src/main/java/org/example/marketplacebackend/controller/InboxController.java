package org.example.marketplacebackend.controller;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;

import org.example.marketplacebackend.DTO.outgoing.InboxGetAllResponseDTO;
import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.model.Inbox;
import org.example.marketplacebackend.repository.InboxRepository;
import org.example.marketplacebackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.comparator.Comparators;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("v1/inbox")
@CrossOrigin(origins = {"localhost:3000", "localhost:8080"}, allowCredentials = "true")
@RestController
public class InboxController {

  private final UserService userService;
  private final InboxRepository inboxRepository;

  public InboxController(UserService userService, InboxRepository inboxRepository) {
    //TODO: ASK IF NEEDED
    this.inboxRepository = inboxRepository;
    this.userService = userService;
  }

  //TODO: POST, DELETE, GET
/*
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

 */

  @GetMapping("")
  public ResponseEntity<?> getMessages(Principal user) {
    Account authenticatedUser = userService.getAccountOrException(user.getName());

    List<Inbox> allInbox = inboxRepository.findByReceiver(authenticatedUser);

    List<InboxGetAllResponseDTO> messages = allInbox
        .stream()
        .sorted((Comparator.comparing(Inbox::getSentAt)))
        .map(inboxEntry -> new InboxGetAllResponseDTO(
            inboxEntry.getId(), inboxEntry.getMessage(),
            inboxEntry.isRead(), inboxEntry.getSentAt())
        )
        .toList();

    if (messages.isEmpty()){
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    return ResponseEntity.status(HttpStatus.OK).body(messages);
  }
}