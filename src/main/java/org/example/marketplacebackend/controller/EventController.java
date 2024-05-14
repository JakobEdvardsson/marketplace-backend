package org.example.marketplacebackend.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/events")
@CrossOrigin(origins = {
    "http://localhost:3000, https://marketplace.johros.dev"}, allowCredentials = "true")
public class EventController {
}
