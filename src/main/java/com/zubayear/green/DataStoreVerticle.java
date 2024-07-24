package com.zubayear.green;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgBuilder;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Tuple;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class DataStoreVerticle extends AbstractVerticle {
    private final Logger logger = Logger.getLogger(DataStoreVerticle.class.getName());

    public static PgConnectOptions pgConnectOptions() {
        return new PgConnectOptions()
                .setPort(5432)
                .setHost("127.0.0.1")
                .setDatabase("vertx")
                .setUser("postgres")
                .setPassword("")
                .setReconnectAttempts(3)
                .setReconnectInterval(2000);
    }

    private static PoolOptions poolOptions() {
        return new PoolOptions()
                .setConnectionTimeout(20000)
                .setConnectionTimeoutUnit(TimeUnit.MILLISECONDS)
                .setIdleTimeout(600000)
                .setConnectionTimeoutUnit(TimeUnit.MILLISECONDS)
                .setMaxLifetime(3600000)
                .setMaxLifetimeUnit(TimeUnit.MILLISECONDS)
                .setMaxSize(50);
    }

    @Override
    public void start(Promise<Void> startPromise) {

        Pool pool = PgBuilder.pool()
                .connectingTo(pgConnectOptions())
                .with(poolOptions())
                .using(vertx)
                .build();

//        EventBus eventBus = vertx.eventBus();
//        eventBus.<JsonObject>consumer("sms.topic", msg -> {
//            JsonObject body = msg.body();
//            String src = body.getString("src");
//            String dst = body.getString("dst");
//            String content = body.getString("content");
//
//            pool.withConnection(conn -> conn.preparedQuery("INSERT INTO public.notifications (src, dst, content) VALUES ($1, $2, $3)")
//                            .execute(Tuple.of(src, dst, content))
////                    .onSuccess(rows -> System.out.println(rows.size()))
//                            .onFailure(throwable -> System.out.println(throwable.getMessage()))
//            );
//
//
//        });


    }
}
