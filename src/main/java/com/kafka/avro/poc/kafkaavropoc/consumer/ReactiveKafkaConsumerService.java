package com.kafka.avro.poc.kafkaavropoc.consumer;

import com.kafka.avro.poc.kafkaavropoc.model.User;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;

@Service
public class ReactiveKafkaConsumerService {

    private final Flux<User> kafkaFlux;

    public ReactiveKafkaConsumerService(KafkaReceiver<String, User> kafkaReceiver) {
        this.kafkaFlux = kafkaReceiver.receive()
                .map(ConsumerRecord::value)  // Extract Avro User object
                .doOnNext(user -> System.out.println("Received: " + user))
                .share(); // Multicast the stream for SSE
    }

    public Flux<User> getUserStream() {
        return kafkaFlux;
    }
}