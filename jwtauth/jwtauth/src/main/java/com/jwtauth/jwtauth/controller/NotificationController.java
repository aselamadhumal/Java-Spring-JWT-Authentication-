package com.jwtauth.jwtauth.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Iterator;

@Slf4j
@RestController
@RequestMapping("/notifications")
public class NotificationController {
    //private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    // Thread-safe list of active SSE emitters
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    // Endpoint to create SSE connection
    @GetMapping("/subscribe")
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(60_000L); // 60 seconds timeout
        emitters.add(emitter);

        log.info("New client subscribed, total clients: {}", emitters.size());

        emitter.onCompletion(() -> {
            emitters.remove(emitter);
            log.info("Client disconnected (completed), remaining clients: {}", emitters.size());
        });

        emitter.onTimeout(() -> {
            emitters.remove(emitter);
            log.warn("Client disconnected (timeout), remaining clients: {}", emitters.size());
        });

        return emitter;
    }

    // Endpoint to send notifications
    @GetMapping("/send")
    public String publishNotification(@RequestParam String message) {
        log.info("Publishing notification: {}", message);

        Iterator<SseEmitter> iterator = emitters.iterator();
        while (iterator.hasNext()) {
            SseEmitter emitter = iterator.next();
            try {
                emitter.send(SseEmitter.event().data(message));
                log.info("Notification sent to client.");
            } catch (IOException e) {
                log.warn("Client disconnected (IOException), removing emitter.");
                iterator.remove();
            } catch (IllegalStateException e) {
                log.warn("Emitter already completed, removing.");
                iterator.remove();
            } catch (Exception e) {
                log.error("Error sending notification: {}", e.getMessage(), e);
                emitter.completeWithError(e);
                iterator.remove();
            }
        }

        return "Notification sent: " + message;
    }
}
