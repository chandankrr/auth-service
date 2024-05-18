package com.chandankrr.authservice.producer;

import com.chandankrr.authservice.dto.UserInfoDto;
import com.chandankrr.authservice.dto.UserInfoEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInfoProducer {

    private final KafkaTemplate<String, UserInfoDto> kafkaTemplate;

    @Value("${spring.kafka.topic-json.name}")
    private String TOPIC_NAME;

    public void sendEventToKafka(UserInfoEvent userInfoEvent) {
        Message<UserInfoEvent> message = MessageBuilder.withPayload(userInfoEvent)
                .setHeader(KafkaHeaders.TOPIC, TOPIC_NAME)
                .build();
        kafkaTemplate.send(message);
    }
}
