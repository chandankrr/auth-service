package com.chandankrr.authservice.serializer;

import com.chandankrr.authservice.dto.UserInfoEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class UserInfoSerializer implements Serializer<UserInfoEvent> {

    private static final Logger logger = LoggerFactory.getLogger(UserInfoSerializer.class);

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Serializer.super.configure(configs, isKey);
    }

    @Override
    public byte[] serialize(String s, UserInfoEvent userInfoEvent) {
        byte[] bytes = null;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            bytes = objectMapper.writeValueAsString(userInfoEvent).getBytes();
        } catch (Exception e) {
            logger.error("Error occurred during serialization: {}", e.getMessage(), e);
        }

        return bytes;
    }

    @Override
    public byte[] serialize(String topic, Headers headers, UserInfoEvent data) {
        return Serializer.super.serialize(topic, headers, data);
    }

    @Override
    public void close() {
        Serializer.super.close();
    }
}
