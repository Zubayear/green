package com.zubayear.green;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;

import java.util.*;
import java.util.logging.Logger;

public class HttpServerVerticle extends AbstractVerticle {
    private static final Logger logger = Logger.getLogger(HttpServerVerticle.class.getName());

    @Override
    public void start(Promise<Void> startPromise) {

//        KafkaProducer<String, String> producer = KafkaProducer.create(vertx, kafkaConfig());

        HttpServer httpServer = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());

        // routing
        router.get("/health")
                .handler(this::healthHandler);
        router.get("/which-shard")
                .handler(this::shardHandler);
        router.post("/send/sms")
                .handler(this::smsHandler);
        router.post("/consistent-hash/servers")
                .handler(this::consistentHandler);


        httpServer.requestHandler(router)
                .listen(0, http -> {
                    if (http.succeeded()) {
                        startPromise.complete();
                        logger.info("Http server started on: " + http.result().actualPort());
                    } else {
                        startPromise.fail(http.cause());
                    }
                });
    }

    private void shardHandler(RoutingContext routingContext) {
        List<String> keys = routingContext.queryParam("key");
        if (keys.isEmpty()) {
            routingContext.response()
                    .putHeader("content-type", "application/json")
                    .setStatusCode(400)
                    .end(new JsonObject().put("msg", "please provide key 😫").encode());
        }
        String key = keys.get(0);
        JsonObject payload = new JsonObject()
                .put("key", key);

        vertx.eventBus().<JsonObject>request("get.node", payload, reply -> {
            if (reply.succeeded()) {
                JsonObject body = reply.result().body();
                routingContext.response()
                        .putHeader("content-type", "application/json")
                        .end(body.encode());
            } else {
                routingContext.response()
                        .putHeader("content-type", "application/json")
                        .setStatusCode(404)
                        .end(new JsonObject().put("msg", "sorry didn't get what you asked for 😔🥲").encode());
            }
        });
    }

    private void consistentHandler(RoutingContext routingContext) {
        JsonObject requestBody = routingContext.body().asJsonObject();

        vertx.eventBus().<JsonObject>request("add.servers", requestBody, reply -> {
            if (reply.succeeded()) {
                JsonObject body = reply.result().body();
                routingContext.response()
                        .putHeader("content-type", "application/json")
                        .end(body.encode());
            } else {
                if (reply.failed()) {
                    routingContext.response()
                            .putHeader("content-type", "application/json")
                            .end(new JsonObject().put("msg", reply.cause().getMessage()).encode());
                } else {
                    routingContext.response()
                            .putHeader("content-type", "application/json")
                            .setStatusCode(500)
                            .end(new JsonObject().put("msg", "something bad happened, but you don't worry. Let us investigate this 🙂").encode());
                }
            }
        });
    }

//    private static Map<String, String> kafkaConfig() {
//        Map<String, String> config = new HashMap<>();
//        config.put("bootstrap.servers", "localhost:9092");
//
//        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        config.put("acks", "1");
//
//        return config;
//
//    }

    private void smsHandler(RoutingContext routingContext) {
        JsonObject jsonObject = routingContext.body().asJsonObject();

        JsonArray dst = jsonObject.getJsonArray("dst");
        String content = jsonObject.getString("content");
        String src = jsonObject.getString("src");

        for (int i = 0; i < dst.size(); i++) {
            JsonObject payload = new JsonObject()
                    .put("content", content)
                    .put("src", src)
                    .put("dst", dst.getString(i));
            vertx.eventBus().send("transactional.messages", payload);
        }

        routingContext.response()
                .putHeader("content-type", "application/json")
                .end(new JsonObject().put("msgId", UUID.randomUUID().toString()).encode());

    }

    private void smsHandler(RoutingContext routingContext, KafkaProducer<String, String> producer) {
        JsonObject jsonObject = routingContext.body().asJsonObject();

        JsonArray dst = jsonObject.getJsonArray("dst");
        String content = jsonObject.getString("content");
        String src = jsonObject.getString("src");

        for (int i = 0; i < dst.size(); i++) {
            JsonObject payload = new JsonObject()
                    .put("content", content)
                    .put("src", src)
                    .put("dst", dst.getString(i));
            producer.send(KafkaProducerRecord.create("transactional_topic", payload.encode()));
        }

        routingContext.response()
                .putHeader("content-type", "application/json")
                .end(new JsonObject().put("msgId", UUID.randomUUID().toString()).encode());

    }

    private void healthHandler(RoutingContext routingContext) {
        routingContext.response()
                .putHeader("content-type", "application/json")
                .end(new JsonObject()
                        .put("status", "up 😎")
                        .put("ts", new Date().toString())
                        .encode()
                );
    }

    @Override
    public void stop() {
        logger.info("Stopping Http server...");
    }
}
