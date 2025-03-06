package com.kafka.avro.poc.kafkaavropoc.controller;

import com.github.javafaker.Faker;
import com.kafka.avro.poc.kafkaavropoc.consumer.ReactiveKafkaConsumerService;
import com.kafka.avro.poc.kafkaavropoc.model.User;
import com.kafka.avro.poc.kafkaavropoc.producer.UserProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class UserStreamController {
    private final ReactiveKafkaConsumerService kafkaConsumerService;
    private final UserProducer userProducer;
    private final Faker faker = new Faker();

    @GetMapping("users/produce")
    public ResponseEntity<String> sendUser() {
        var user = new User(
                faker.number().randomDigitNotZero(), // Random ID
                faker.name().fullName(),             // Random Name
                faker.internet().emailAddress()      // Random Email
        );
        userProducer.sendUserMessage(user);
        return ResponseEntity.ok("User sent!");
    }

    @GetMapping(value = "users/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamUsers() {
        return kafkaConsumerService.getUserStream()
                .map(u -> u.getName() + " | " + u.getEmail());
    }
}