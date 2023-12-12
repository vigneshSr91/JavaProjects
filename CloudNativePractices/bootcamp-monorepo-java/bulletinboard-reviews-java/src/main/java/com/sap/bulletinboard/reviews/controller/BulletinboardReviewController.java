package com.sap.bulletinboard.reviews.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.bulletinboard.reviews.config.KafkaProducerConfig;
import com.sap.bulletinboard.reviews.controller.dto.AverageRatingDto;
import com.sap.bulletinboard.reviews.controller.dto.ReviewDto;
import com.sap.bulletinboard.reviews.models.Review;
import com.sap.bulletinboard.reviews.repository.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class BulletinboardReviewController {
    @Autowired
    ReviewRepository repository;

    @Autowired
    private  AverageRatingService averageRatingService;
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @GetMapping("/reviews")
    public List<ReviewDto> getAllReviews() {
        return repository.findAll().stream().map(this::entityToDto).collect(Collectors.toList());
    }

    @GetMapping("/reviews/{reviewee}")
    public List<ReviewDto> getReviewsForReviewee(@PathVariable String reviewee) {
        return repository.findByIdRevieweeEmail(reviewee).stream().map(this::entityToDto).collect(Collectors.toList());
    }

    @PostMapping("/reviews")
    public ResponseEntity<Object> appendReview(@RequestBody ReviewDto reviewDto) throws JsonProcessingException {
        Review review = dtoToEntity(reviewDto);
        URI location = URI.create("reviews:" + review.getId());
        if (!repository.existsById(review.getId())) {
            repository.save(review);
            KafkaTemplate kafkaTemplate = new KafkaProducerConfig().kafkaTemplate();
            KafkaController kafkaController = new KafkaController(kafkaTemplate);
            Number avgRating = repository.getAvgRatingByIdRevieweeEmail(reviewDto.getRevieweeEmail());
            String contact = reviewDto.getRevieweeEmail();
            Map<String, String> rating = new HashMap<String, String>();
            rating.put("contact", contact);
            rating.put("avgRating", avgRating.toString());
            ObjectMapper objectMapper = new ObjectMapper();
            String message = objectMapper.writeValueAsString(rating);
            kafkaController.sendMessage(message);
            kafkaTemplate.flush();

            return ResponseEntity.created(location).build();
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).location(location).build();
        }
    }

    @DeleteMapping("/reviews")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeReviews() {
        repository.deleteAll();
    }

    @GetMapping("/averageRatings/{reviewee}")
    public AverageRatingDto getAllReviews(@PathVariable String reviewee) {
        /*
        Number averageRating = repository.getAvgRatingByIdRevieweeEmail(reviewee);
        if (averageRating == null) {
            logger.info("No ratings found for {}", reviewee);
        }
        return new AverageRatingDto(averageRating);
         */
        return averageRatingService.getAverageRatingDto(reviewee);
    }

    private ReviewDto entityToDto(Review review) {
        ReviewDto reviewDto = new ReviewDto();
        BeanUtils.copyProperties(review, reviewDto);
        BeanUtils.copyProperties(review.getId(), reviewDto);
        return reviewDto;
    }

    private Review dtoToEntity(ReviewDto reviewDto) {
        Review review = new Review();
        BeanUtils.copyProperties(reviewDto, review);
        review.setId(new Review.ReviewIdentity());
        BeanUtils.copyProperties(reviewDto, review.getId());
        return review;
    }
}
