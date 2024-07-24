package com.zubayear.green;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;

import java.util.HashMap;
import java.util.Map;

public class ProducerVerticle extends AbstractVerticle {


    private static Map<String, String> kafkaConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092");

        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("acks", "1");

        return config;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        KafkaProducer<String, String> producer = KafkaProducer.create(vertx, kafkaConfig());
        EventBus eventBus = vertx.eventBus();
        eventBus.<JsonObject>consumer("transactional.messages", msg -> producer.send(KafkaProducerRecord.create("transactional_topic", msg.body().encode())));
    }
}