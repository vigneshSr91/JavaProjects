package com.sap.bulletinboard.ads.services;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.bulletinboard.ads.commons.Constant;
import com.sap.bulletinboard.ads.models.AverageRatingDb;
import com.sap.bulletinboard.ads.models.AverageRatingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
@Component
public class KafkaConsumer {
    public final String topicName= Constant.topicname;
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    AverageRatingRepository repository;
    @KafkaListener(topics = topicName, groupId = "001")
    public void consume(String message) throws JsonProcessingException {
        logger.info("recieved message " + message);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> receivedMessage =  objectMapper.readValue(message,
                new TypeReference<HashMap<String, String>>() {});
        AverageRatingDb averageRating = new AverageRatingDb();
        averageRating.setContact(receivedMessage.get("contact"));
        averageRating.setAvgRating(receivedMessage.get("avgRating"));
        repository.save(averageRating);

    }
}
