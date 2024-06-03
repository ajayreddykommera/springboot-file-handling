package com.ajay.file_handling.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import org.bson.UuidRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;


@Configuration
public class MongoConfig {

    @Value("${mongo.connectionString}")
    private String connectionString;

    @Value("${mongo.db_name}")
    private String databaseName;

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoDatabaseFactory(databaseName));
    }

    private MongoDatabaseFactory mongoDatabaseFactory(String databaseName) {
        MongoClientSettings settings = MongoClientSettings.builder()
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .applyConnectionString(new ConnectionString(connectionString))
                .build();
        return new SimpleMongoClientDatabaseFactory(MongoClients.create(settings), databaseName);
    }

}
