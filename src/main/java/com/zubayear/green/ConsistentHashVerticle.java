package com.zubayear.green;

import com.google.common.hash.Hashing;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

public class ConsistentHashVerticle extends AbstractVerticle {

    private final int totalSlots = 50;
    private final List<StorageNode> nodes = new ArrayList<>();
    private final List<Integer> keys = new ArrayList<>();

    @Override
    public void start(Promise<Void> startPromise) {
        vertx.eventBus().<JsonObject>consumer("add.servers", msg -> {
            JsonObject body = msg.body();
            JsonArray serverList = body.getJsonArray("serverList");
            String operation = body.getString("operation");
            int size = serverList.size();
            if (operation.equalsIgnoreCase("add")) {
                for (int i = 0; i < size; i++) {
                    JsonObject jsonObject = serverList.getJsonObject(i);
                    String name = jsonObject.getString("name");
                    String ip = jsonObject.getString("ip");
                    StorageNode storageNode = new StorageNode(name, ip);
                    addNode(storageNode);
                }
                JsonObject response = new JsonObject()
                        .put("msg", "server list added 🥳🎉");
                msg.reply(response);
            } else if (operation.equalsIgnoreCase("remove")) {
                for (int i = 0; i < size; i++) {
                    JsonObject jsonObject = serverList.getJsonObject(i);
                    String name = jsonObject.getString("name");
                    String ip = jsonObject.getString("ip");
                    StorageNode storageNode = new StorageNode(name, ip);
                    removeNode(storageNode);
                }
                JsonObject response = new JsonObject()
                        .put("msg", "server list removed 🥳🎉");
                msg.reply(response);
            } else {
                msg.fail(400, "invalid operation 🤭");
            }
        });

        vertx.eventBus().<JsonObject>consumer("get.node", msg -> {
            JsonObject body = msg.body();
            String key = body.getString("key");
            StorageNode node = assign(key);

            JsonObject payload = new JsonObject()
                    .put("name", node.name)
                    .put("id", node.ip);

            msg.reply(payload);
        });
    }

    private int hashFun(String key) {
        return Math.abs(Hashing.sha256()
                .hashString(key, StandardCharsets.UTF_8)
                .asInt()) % totalSlots;
    }

    private int addNode(StorageNode node) {
        /*
         * keys: [15,30]
         * nodes: [Node1,Node2]
         * if Node3 is hashed to key 10
         * then
         * keys: [10,15,30]
         * nodes: [Node3,Node1,Node2]
         * we need to find the index efficiently with binary search
         * */
        if (keys.size() == totalSlots) {
            throw new RuntimeException("hash space is full");
        }
        int key = hashFun(node.ip);

        int insertIdx = Collections.binarySearch(keys, key);

        // hash collision
        if (insertIdx >= 0) {
            throw new RuntimeException("collision occurred");
        } else {
            insertIdx = -insertIdx - 1;
        }
        nodes.add(insertIdx, node);
        keys.add(insertIdx, key);

        return key;
    }

    private int removeNode(StorageNode node) {
        if (keys.isEmpty()) {
            throw new RuntimeException("hash space is empty");
        }
        int key = hashFun(node.ip);
        int idxToRemove = Collections.binarySearch(keys, key);

        if (idxToRemove < 0 || !keys.get(idxToRemove).equals(key)) {
            throw new NoSuchElementException("node does not exists");
        }

        keys.remove(idxToRemove);
        nodes.remove(idxToRemove);

        return key;
    }

    private StorageNode assign(String item) {
        /*
         * Given an item, the method returns the node it is associated with
         * */
        int key = hashFun(item);
        int idx = Collections.binarySearch(keys, key);

        if (idx < 0) {
            idx = -idx - 1;
        } else {
            idx = idx + 1;
        }
        idx = idx % keys.size();
        return nodes.get(idx);
    }
}
