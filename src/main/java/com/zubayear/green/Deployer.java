package com.zubayear.green;
//
//import io.vertx.core.AbstractVerticle;
//import io.vertx.core.Vertx;
//
//import java.util.logging.Logger;
//
//public class Deployer extends AbstractVerticle {
//
//    private final static Logger logger = Logger.getLogger(Deployer.class.getName());
//    @Override
//    public void start() {
//        long delay = 1000;
//        for (int i = 0; i < 4; i++) {
//            vertx.setTimer(delay, id -> deploy());
//            delay += 1000;
//        }
//    }
//
//    private void deploy() {
//        vertx.deployVerticle(new MainVerticle(), ar -> {
//            if (ar.succeeded()) {
//                String id = ar.result();
//                logger.info("Successfully deployed: " + id);
//            } else {
//                logger.info("Error while deploying: " + ar.cause());
//            }
//        });
//
//        vertx.deployVerticle(new Heatsensor(), ar -> {
//            if (ar.succeeded()) {
//                String id = ar.result();
//                logger.info("Successfully deployed: " + id);
//            } else {
//                logger.info("Error while deploying: " + ar.cause());
//            }
//        });
//
//        vertx.deployVerticle(new Listener(), ar -> {
//            if (ar.succeeded()) {
//                String id = ar.result();
//                logger.info("Successfully deployed: " + id);
//            } else {
//                logger.info("Error while deploying: " + ar.cause());
//            }
//        });
//    }
//
//    private void undeploy(String id) {
//        vertx.undeploy(id, ar -> {
//            if (ar.succeeded()) {
//                logger.info("Undeploy: " + id);
//            } else {
//                logger.info("Could not undeploy: " + id);
//            }
//        });
//    }
//
//    public static void main(String[] args) {
//        Vertx vertx1 = Vertx.vertx();
//        vertx1.deployVerticle(new MainVerticle());
//        vertx1.deployVerticle(new Heatsensor());
//        vertx1.deployVerticle(new Listener());
//    }
//}



