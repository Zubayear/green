package com.zubayear.green;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;

public class MainVerticle extends AbstractVerticle {
    @Override
    public void start() {
        DeploymentOptions deploymentOptions = new DeploymentOptions()
                .setInstances(1);
        vertx.deployVerticle("com.zubayear.green.HttpServerVerticle");
//        vertx.deployVerticle("com.zubayear.green.DataStoreVerticle", deploymentOptions);
        vertx.deployVerticle("com.zubayear.green.ConsistentHashVerticle", deploymentOptions);
    }
}

