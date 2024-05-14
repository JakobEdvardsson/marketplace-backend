package org.example.marketplacebackend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.marketplacebackend.DTO.incoming.EventDTO;
import org.example.marketplacebackend.service.EmitterService;
import org.example.marketplacebackend.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RequestMapping("v1/events")
@CrossOrigin(origins = {
    "http://localhost:3000, https://marketplace.johros.dev"}, allowCredentials = "true")
@RestController
@RequiredArgsConstructor
public class EventController {
  public static final String MEMBER_ID_HEADER = "MemberId";

  private final EmitterService emitterService;
  private final NotificationService notificationService;

  @GetMapping
  public SseEmitter subscribeToEvents(@RequestHeader(name = MEMBER_ID_HEADER) String memberId) {
    log.debug("Subscribing member with id {}", memberId);
    return emitterService.createEmitter(memberId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void publishEvent(@RequestHeader(name = MEMBER_ID_HEADER) String memberId, @RequestBody EventDTO event) {
    log.debug("Publishing event {} for member with id {}", event, memberId);
    notificationService.sendNotification(memberId, event);
  }
}
