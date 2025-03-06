# Getting Started

### Reference Documentation

This repo demonstrate to use avro schema registry in java implementations

* Apache Kafka
* Avro Schema Registry
* Avro model
* Reactive kafka avro consumer
* Kafka avro producer

### Dependencies
```xml
    <dependency>
        <groupId>org.springframework.kafka</groupId>
        <artifactId>spring-kafka</artifactId>
    </dependency>
    
    <!-- Avro Serializer & Deserializer -->
    <dependency>
        <groupId>io.confluent</groupId>
        <artifactId>kafka-avro-serializer</artifactId>
        <version>7.9.0</version>
    </dependency>
    
    <!-- Avro Compiler Plugin -->
    <dependency>
        <groupId>org.apache.avro</groupId>
        <artifactId>avro</artifactId>
        <version>1.12.0</version>
    </dependency>
```

### Maven Plugin
```
<plugin>
    <groupId>org.apache.avro</groupId>
    <artifactId>avro-maven-plugin</artifactId>
    <version>1.11.3</version>
    <executions>
        <execution>
            <phase>generate-sources</phase>
            <goals>
                <goal>schema</goal>
            </goals>
            <configuration>
                <sourceDirectory>${project.basedir}/src/main/resources/avro</sourceDirectory>
                <outputDirectory>${project.basedir}/src/main/java</outputDirectory>
            </configuration>
        </execution>
    </executions>
</plugin>
```
### Avro file
```json
{
  "type": "record",
  "name": "User",
  "namespace": "com.kafka.avro.poc.kafkaavropoc.model",
  "fields": [
    { "name": "id", "type": "int" },
    { "name": "name", "type": "string" },
    { "name": "email", "type": "string" }
  ]
}
```

### Generate java classes from avro files
```
If you place the avro plugin in pom.xml, you can run below command to generate java classes from avro files
> mvn clean compile 

Avro plugin extracts java classes specified location on avro file
```

## Consumer Implementations
* Reactive Consumer
```java
@Bean
public KafkaReceiver<String, User> kafkaReceiver() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "reactive-user-group");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, io.confluent.kafka.serializers.KafkaAvroDeserializer.class);
    props.put("schema.registry.url", "http://localhost:8081");
    props.put("specific.avro.reader", true);

    ReceiverOptions<String, User> receiverOptions = ReceiverOptions.<String, User>create(props)
            .subscription(Collections.singleton("users-topic"));

    return KafkaReceiver.create(receiverOptions);
}
```
* Non Reactive Consumer Implementation
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    properties:
      schema.registry.url: http://localhost:8081
    consumer:
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: io.confluent.kafka.serializers.KafkaAvroDeserializer
      specific-avro-reader: true
```
```java   
@KafkaListener(topic="topic1")
public void consume(@Payload User payload){}
```

### Producer Config
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: io.confluent.kafka.serializers.KafkaAvroSerializer
    properties:
      schema.registry.url: http://localhost:8081
```
```java
@Service
@RequiredArgsConstructor
public class UserProducer {
    private final KafkaTemplate<String, User> kafkaTemplate;

    public void sendUserMessage(User user) {
        kafkaTemplate.send("users-topic", user);
        System.out.println("Sent user: " + user);
    }
}
```

### Run
```shell
docker compose up -d
mvn clean compile
mvn spring-boot:run
```

### Requests
```shell
-- produce
curl http://localhost:9909/users/produce

-- consume
curl http://localhost:9909/users/stream
```