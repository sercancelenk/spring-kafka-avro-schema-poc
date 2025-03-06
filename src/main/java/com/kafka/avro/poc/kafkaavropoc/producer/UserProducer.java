package com.kafka.avro.poc.kafkaavropoc.producer;

import com.kafka.avro.poc.kafkaavropoc.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProducer {
    private final KafkaTemplate<String, User> kafkaTemplate;

    public void sendUserMessage(User user) {
        kafkaTemplate.send("users-topic", user);
        System.out.println("Sent user: " + user);
    }
}