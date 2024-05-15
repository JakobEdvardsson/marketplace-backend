package org.example.marketplacebackend.controller;

import org.example.marketplacebackend.utility.ProductChangeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

@RestController
public class SseController {

  private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

  @GetMapping("/sse")
  public SseEmitter streamSseMvc() {
    SseEmitter emitter = new SseEmitter();
    emitters.add(emitter);

    emitter.onCompletion(() -> emitters.remove(emitter));
    emitter.onTimeout(() -> emitters.remove(emitter));

    return emitter;
  }

  @EventListener
  public void handleEntityPersistedEvent(ProductChangeEvent event) {
    System.out.println("Sending product data to all subscribers");
    sendEvent(event);
  }

  public void sendEvent(ProductChangeEvent event) {
    List<SseEmitter> deadEmitters = new ArrayList<>();
    for (SseEmitter emitter : emitters) {
      try {
        System.out.println("Sending SSE event for entity: " + event.getEntity());
        emitter.send(SseEmitter.event().name("entity-persisted").data(event.getEntity()));
      } catch (IOException e) {
        deadEmitters.add(emitter);
      }
    }
    emitters.removeAll(deadEmitters);
  }
}
