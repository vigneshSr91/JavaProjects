package com.sap.bulletinboard.ads.controllers;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import com.sap.bulletinboard.ads.controllers.dto.AdvertisementDto;
import com.sap.bulletinboard.ads.controllers.dto.PageHeaderBuilder;
import com.sap.bulletinboard.ads.models.Advertisement;
import com.sap.bulletinboard.ads.models.AdvertisementRepository;
import com.sap.bulletinboard.ads.models.AverageRatingDb;
import com.sap.bulletinboard.ads.models.AverageRatingRepository;
import com.sap.bulletinboard.ads.services.ReviewsServiceClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@Validated
@RequestMapping("/api/v1/ads")
public class AdvertisementController {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    static final String PATH = "/api/v1/ads";
    static final String PATH_PAGES = PATH + "/pages/";
    static final int DEFAULT_PAGE_SIZE = 20;
    private static final int FIRST_PAGE_ID = 0;

    @Value("${REVIEWS_HOST:#{null}}")
    private String reviewsServiceHost;

    private final AdvertisementRepository repository;
    private final ReviewsServiceClient reviewsServiceClient;

    @Autowired
    private AverageRatingRepository averageRatingRepository;

    public AdvertisementController(AdvertisementRepository repository, ReviewsServiceClient reviewsServiceClient) {
        this.repository = repository;
        this.reviewsServiceClient = reviewsServiceClient;
    }

    @GetMapping
    public ResponseEntity<List<AdvertisementDto>> advertisements() {
        return advertisementsForPage(FIRST_PAGE_ID);
    }

    @GetMapping("/pages/{pageId}")
    public ResponseEntity<List<AdvertisementDto>> advertisementsForPage(@PathVariable("pageId") int pageId) {
        Page<Advertisement> page = repository.findAll(PageRequest.of(pageId, DEFAULT_PAGE_SIZE));

        List<AdvertisementDto> dtos = page.getContent().stream().map(this::entityToDto).collect(Collectors.toList());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LINK, PageHeaderBuilder.createLinkHeaderString(page, PATH_PAGES));
        return new ResponseEntity<>(dtos, headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public AdvertisementDto advertisementById(@PathVariable("id") @Min(0) Long id) {
        throwIfNotExisting(id);
        return entityToDto(repository.findById(id).get());
    }

    @PostMapping
    public ResponseEntity<AdvertisementDto> add(@RequestBody @Valid AdvertisementDto advertisement,
                                                UriComponentsBuilder uriComponentsBuilder) {
        logger.info(advertisement.toString());
        AdvertisementDto savedAdvertisement = entityToDto(repository.save(dtoToEntity(advertisement)));
        UriComponents uriComponents = uriComponentsBuilder.path(PATH + "/{id}").buildAndExpand(savedAdvertisement.id);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uriComponents.toUri());
        return new ResponseEntity<>(savedAdvertisement, headers, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public AdvertisementDto update(@RequestBody AdvertisementDto updatedAdvertisement, @PathVariable("id") Long id) {
        throwIfInconsistent(id, updatedAdvertisement.id);
        throwIfNotExisting(id);
        return entityToDto(repository.save(dtoToEntity(updatedAdvertisement)));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteById(@PathVariable("id") Long id) {
        throwIfNotExisting(id);
        repository.deleteById(id);
    }

    @DeleteMapping
    @ResponseStatus(NO_CONTENT)
    public void deleteAll() {
        repository.deleteAllInBatch();
    }

    private void throwIfNotExisting(@PathVariable("id") Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("no Advertisement with id " + id);
        }
    }

    private void throwIfInconsistent(Long expected, Long actual) {
        if (!expected.equals(actual)) {
            String message = "bad request, inconsistent IDs between request and object: request id = " + expected
                    + ", object id = " + actual;
            throw new BadRequestException(message);
        }
    }

    private AdvertisementDto entityToDto(Advertisement ad) {
        AdvertisementDto dto = new AdvertisementDto();

        dto.id = ad.getId();
        dto.title = ad.getTitle();
        dto.contact = ad.getContact();
        //Double averageRating = reviewsServiceClient.getAverageRating(dto.contact);
        Double averageRating = null;
        try{
            AverageRatingDb averageRatingDb = averageRatingRepository.findById(dto.contact).get();
            averageRating = Double.parseDouble(averageRatingDb.getAvgRating());
        } catch (NoSuchElementException E) {
            averageRating = 1d;
            logger.info("User does not have a rating yet, defaulting to 1 (=untrusted)");
        }

        dto.averageContactRating = averageRating;

        dto.price = ad.getPrice();
        dto.currency = ad.getCurrency();
        dto.reviewsUrl = reviewsServiceHost + "/#/reviews/" + ad.getContact();

        dto.metadata.createdAt = Objects.toString(ad.getCreatedAt());
        dto.metadata.modifiedAt = Objects.toString(ad.getModifiedAt());
        dto.metadata.version = ad.getVersion();

        return dto;
    }

    private Advertisement dtoToEntity(AdvertisementDto dto) {
        // does not map "read-only" attributes
        Advertisement ad = new Advertisement();
        ad.setId(dto.id);
        ad.setVersion(dto.metadata.version);
        ad.setTitle(dto.title);
        ad.setContact(dto.contact);
        ad.setPrice(dto.price);
        ad.setCurrency(dto.currency);
        return ad;
    }
}
