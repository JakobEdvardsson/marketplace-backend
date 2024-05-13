package org.example.marketplacebackend.service;

import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.model.Inbox;
import org.example.marketplacebackend.repository.InboxRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InboxService {
  private final InboxRepository inboxRepository;
  private final UserService userService;

  public InboxService(InboxRepository inboxRepository, UserService userService) {
    this.inboxRepository = inboxRepository;
    this.userService = userService;
  }

  public void newMessageReceived(Inbox message) {
    inboxRepository.save(message); // Save message to database
    userService.broadcastToUser(message.getReceiver().getId(), message.getMessage()); // User get notified
  }
}
