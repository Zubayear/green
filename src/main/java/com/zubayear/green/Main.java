package com.zubayear.green;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

public class Main {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        DeploymentOptions deploymentOptions = new DeploymentOptions()
                .setInstances(1);
        vertx.deployVerticle("com.zubayear.green.HttpServerVerticle", deploymentOptions);
        vertx.deployVerticle("com.zubayear.green.ConsistentHashVerticle", deploymentOptions);
    }
}
