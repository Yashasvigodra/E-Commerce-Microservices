package com.mini.s.product.service.config;

import com.mongodb.client.MongoClient;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class MongoClientDebug {

    private final MongoClient mongoClient;

    public MongoClientDebug(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @PostConstruct
    public void printActualDb() {
        mongoClient.listDatabaseNames()
                .forEach(db -> System.out.println("🧠 Mongo sees DB: " + db));
    }
}