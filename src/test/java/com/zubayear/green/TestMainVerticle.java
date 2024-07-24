package com.zubayear.green;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.HashString;
import io.vertx.ext.auth.impl.hash.SHA1;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.crypto.EncryptedPrivateKeyInfo;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Arrays;

//@ExtendWith(VertxExtension.class)
public class TestMainVerticle {

//  @BeforeEach
//  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
//    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
//  }

//  @Test
//  void verticle_deployed(Vertx vertx, VertxTestContext testContext) throws Throwable {
//    testContext.completeNow();
//  }

    @Test
    void sha() throws InterruptedException {
        Promise<String> promise = Promise.promise();
        Vertx vertx = Vertx.vertx();
        vertx.setTimer(5000, id -> {
            if (System.currentTimeMillis() % 2 == 0) {
                promise.complete("ok");
            } else {
                promise.fail(new RuntimeException("Bad luck..."));
            }
        });

        Future<String> future = promise.future();
        future
                .onSuccess(System.out::println)
                .onFailure(err -> System.out.println(err.getMessage()));

        Thread.sleep(90000);
    }
}
