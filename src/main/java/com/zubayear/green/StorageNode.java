package com.zubayear.green;

public class StorageNode {

    String name, ip;

    public StorageNode(String name, String ip) {
        this.name = name;
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "StorageNode{" +
                "name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }
}
