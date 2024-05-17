package org.example.marketplacebackend.service;

import jakarta.transaction.Transactional;
import org.example.marketplacebackend.model.Inbox;
import org.example.marketplacebackend.repository.InboxRepository;
import org.springframework.stereotype.Service;

@Service
public class InboxService {
  InboxRepository inboxRepository;
  public InboxService(InboxRepository inboxRepository) {
    this.inboxRepository = inboxRepository;
  }

  @Transactional
  public void saveAll(Iterable<Inbox> inboxes) {
    inboxRepository.saveAll(inboxes);
  }

}
