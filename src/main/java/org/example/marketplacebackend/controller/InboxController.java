package org.example.marketplacebackend.controller;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.marketplacebackend.DTO.outgoing.InboxGetAllResponseDTO;
import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.model.Inbox;
import org.example.marketplacebackend.repository.InboxRepository;
import org.example.marketplacebackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1/inbox")
@CrossOrigin(origins = {
    "http://localhost:3000, https://marketplace.johros.dev"}, allowCredentials = "true")
@RestController
public class InboxController {

  private final UserService userService;
  private final InboxRepository inboxRepository;

  public InboxController(
      UserService userService,
      InboxRepository inboxRepository
  ) {
    //TODO: ASK IF NEEDED
    this.inboxRepository = inboxRepository;
    this.userService = userService;
  }

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
 */
  @Transactional
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteMessage(
      Principal principal,
      @PathVariable UUID id
  ) {
    Account authenticatedUser = userService.getAccountOrException(
        principal.getName()
    );
    Long deletedRows = inboxRepository.deleteByIdAndReceiver(
        id,
        authenticatedUser
    );
    if (deletedRows == 0) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    return ResponseEntity.ok().build();
  }

  @GetMapping("")
  public ResponseEntity<?> getMessages(Principal user) {
    Account authenticatedUser = userService.getAccountOrException(
        user.getName()
    );

    List<Inbox> allInbox = inboxRepository.findByReceiver(
        authenticatedUser
    );

    List<InboxGetAllResponseDTO> messages = allInbox
        .stream()
        .sorted((Comparator.comparing(Inbox::getSentAt)))
        .map(
            inboxEntry ->
                new InboxGetAllResponseDTO(
                    inboxEntry.getId(),
                    inboxEntry.getMessage(),
                    inboxEntry.getIsRead(),
                    inboxEntry.getSentAt(),
                    inboxEntry.getProduct().getId()
                )
        )
        .toList();

    if (messages.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    return ResponseEntity.status(HttpStatus.OK).body(messages);
  }

  // TODO: MAKE getMessageById into a POST where you setIsRead!

  //GET - retrieve a specific message in Inbox based on ID
  @GetMapping("/{id}")
  public ResponseEntity<?> getMessageById(
      Principal user,
      @PathVariable UUID id
  ) {
    Account authenticatedUser = userService.getAccountOrException(
        user.getName()
    );

    //Searches for message with ID which also match the receiver
    Optional<Inbox> inbox = inboxRepository.findByProductIdAndReceiver(
        id,
        authenticatedUser
    );
    if (inbox.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    Inbox message = inbox.get();
    InboxGetAllResponseDTO responseDTO = new InboxGetAllResponseDTO(
        message.getId(),
        message.getMessage(),
        message.getIsRead(),
        message.getSentAt(),
        message.getProduct().getId()
    );

    message.setIsRead(true);
    inboxRepository.save(message);

    return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
  }
}