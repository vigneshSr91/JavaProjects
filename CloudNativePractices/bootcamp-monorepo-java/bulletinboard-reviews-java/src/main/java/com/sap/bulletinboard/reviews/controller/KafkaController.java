package com.sap.bulletinboard.reviews.controller;

import commons.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaController {

    //@Value("${spring.kafka.groupid}")
    private String groupId="001";
    private final KafkaTemplate<String, String> kafkaTemplate;
    private Logger logger = LoggerFactory.getLogger(getClass());
    public  KafkaController(KafkaTemplate<String, String> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String message){
        kafkaTemplate.send(Constant.topicname, message);
        logger.info("Message sent to Kafka: " + message);
    }
}
