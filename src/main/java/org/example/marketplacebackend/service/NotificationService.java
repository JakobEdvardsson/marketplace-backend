package org.example.marketplacebackend.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.marketplacebackend.DTO.incoming.EventDTO;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@AllArgsConstructor
@Slf4j
public class NotificationService {

  private final EmitterRepository emitterRepository;
  private final EventMapper eventMapper;

  @Override
  public void sendNotification(String memberId, EventDTO event) {
    if (event == null) {
      log.debug("No server event to send to device.");
      return;
    }
    doSendNotification(memberId, event);
  }

  private void doSendNotification(String memberId, EventDTO event) {
    emitterRepository.get(memberId).ifPresentOrElse(sseEmitter -> {
      try {
        log.debug("Sending event: {} for member: {}", event, memberId);
        sseEmitter.send(eventMapper.toSseEventBuilder(event));
      } catch (IOException | IllegalStateException e) {
        log.debug("Error while sending event: {} for member: {} - exception: {}", event, memberId,
            e);
        emitterRepository.remove(memberId);
      }
    }, () -> log.debug("No emitter for member {}", memberId));
  }
}
