package com.sap.bulletinboard.ads.controllers;


import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.bulletinboard.ads.controllers.dto.AdvertisementDto;
import com.sap.bulletinboard.ads.models.AdvertisementRepository;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class AdvertisementControllerTest {

    private static final String LOCATION = "Location";
    private static final String SOME_TITLE = "MyNewAdvertisement";
    private static final String SOME_OTHER_TITLE = "MyOldAdvertisement";

    @Autowired
    private AdvertisementRepository repo;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    public void tearDown() {
        repo.deleteAll();
    }

    @Test
    public void create() throws Exception {
        mockMvc.perform(buildPostRequest(SOME_TITLE)).andExpect(status().isCreated())
                .andExpect(header().string(LOCATION, is(not(""))))
                .andExpect(content().contentType(APPLICATION_JSON)).andExpect(jsonPath("$.title", is(SOME_TITLE)));
    }

    @Test
    public void createAndGetByLocation() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(buildPostRequest(SOME_TITLE)).andExpect(status().isCreated())
                .andReturn().getResponse();

        mockMvc.perform(get(response.getHeader(LOCATION))).andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(SOME_TITLE)));
    }

    @Test
    public void read_All() throws Exception {
        mockMvc.perform(buildDeleteAllRequest()).andExpect(status().isNoContent());

        mockMvc.perform(buildPostRequest(SOME_TITLE)).andExpect(status().isCreated());
        mockMvc.perform(buildPostRequest(SOME_TITLE)).andExpect(status().isCreated());
        mockMvc.perform(buildPostRequest(SOME_TITLE)).andExpect(status().isCreated());

        mockMvc.perform(buildGetRequest("")).andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON)).andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    public void read_ById_NotFound() throws Exception {
        mockMvc.perform(buildGetRequest("4711")).andExpect(status().isNotFound());
    }

    @Test
    public void read_ById_Negative() throws Exception {
        mockMvc.perform(buildGetRequest("-1")).andExpect(status().isBadRequest());
    }

    @Test
    public void createNullTitle() throws Exception {
        mockMvc.perform(buildPostRequest(null)).andExpect(status().isBadRequest());
    }

    @Test
    public void createBlankTitle() throws Exception {
        mockMvc.perform(buildPostRequest("")).andExpect(status().isBadRequest());
    }

    @Test
    public void createWithNoContent() throws Exception {
        mockMvc.perform(post(AdvertisementController.PATH).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void read_ById() throws Exception {
        String id = performPostAndGetId();

        mockMvc.perform(buildGetRequest(id)).andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON)).andExpect(jsonPath("$.title", is(SOME_TITLE)));
    }

    @Test
    public void updateNotFound() throws Exception {
        AdvertisementDto advertisement = createAdvertisement(SOME_TITLE);
        advertisement.id = 4711L;
        mockMvc.perform(buildPutRequest("4711", advertisement)).andExpect(status().isNotFound());
    }

    @Test
    public void update_ById() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(buildPostRequest(SOME_TITLE)).andExpect(status().isCreated())
                .andReturn().getResponse();

        AdvertisementDto advertisement = convertJsonContent(response, AdvertisementDto.class);
        advertisement.title = SOME_OTHER_TITLE;
        String id = getIdFromLocation(response.getHeader(LOCATION));

        mockMvc.perform(buildPutRequest(id, advertisement)).andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.title", is(SOME_OTHER_TITLE)));
    }

    @Test
    public void updateByNotMatchingId() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(buildPostRequest(SOME_TITLE)).andExpect(status().isCreated())
                .andReturn().getResponse();

        AdvertisementDto advertisement = convertJsonContent(response, AdvertisementDto.class);

        mockMvc.perform(buildPutRequest("1188", advertisement)).andExpect(status().isBadRequest());
    }

    @Test
    public void deleteNotFound() throws Exception {
        mockMvc.perform(buildDeleteRequest("4711")).andExpect(status().isNotFound());
    }

    @Test
    public void deleteById() throws Exception {
        String id = performPostAndGetId();
        mockMvc.perform(buildDeleteRequest(id)).andExpect(status().isNoContent());
        mockMvc.perform(buildGetRequest(id)).andExpect(status().isNotFound());
    }

    @Test
    public void deleteAll() throws Exception {
        String id = performPostAndGetId();
        mockMvc.perform(buildDeleteRequest("")).andExpect(status().isNoContent());
        mockMvc.perform(buildGetRequest(id)).andExpect(status().isNotFound());
    }

    @Test
    public void doNotReuseIdsOfDeletedItems() throws Exception {
        String id = performPostAndGetId();

        mockMvc.perform(buildDeleteRequest(id)).andExpect(status().isNoContent());

        String idNewAd = performPostAndGetId();
        assertThat(idNewAd).isNotEqualTo(id);
    }

    @Test
    public void readAdsFromSeveralPages() throws Exception {
        int adsCount = AdvertisementController.DEFAULT_PAGE_SIZE + 1;

        mockMvc.perform(buildDeleteRequest("")).andExpect(status().isNoContent());

        for (int i = 0; i < adsCount; i++) {
            mockMvc.perform(buildPostRequest(SOME_TITLE)).andExpect(status().isCreated());
        }

        mockMvc.perform(buildGetByPageRequest(0)).andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(AdvertisementController.DEFAULT_PAGE_SIZE)));

        mockMvc.perform(buildGetByPageRequest(1)).andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON)).andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    public void navigatePages() throws Exception {
        int adsCount = (AdvertisementController.DEFAULT_PAGE_SIZE * 2) + 1;

        mockMvc.perform(buildDeleteRequest("")).andExpect(status().isNoContent());

        for (int i = 0; i < adsCount; i++) {
            mockMvc.perform(buildPostRequest(SOME_TITLE)).andExpect(status().isCreated());
        }

        // get query
        String linkHeader = performGetRequest(AdvertisementController.PATH).getHeader(HttpHeaders.LINK);
        assertThat(linkHeader).isEqualTo("</api/v1/ads/pages/1>; rel=\"next\"");

        // navigate to next
        String nextLink = extractLinks(linkHeader).get(0);
        String linkHeader2ndPage = performGetRequest(nextLink).getHeader(HttpHeaders.LINK);
		assertThat(linkHeader2ndPage)
				.isEqualTo("</api/v1/ads/pages/0>; rel=\"previous\", </api/v1/ads/pages/2>; rel=\"next\"");

        // navigate to next
        nextLink = extractLinks(linkHeader2ndPage).get(1);
        String linkHeader3rdPage = performGetRequest(nextLink).getHeader(HttpHeaders.LINK);
        assertThat(linkHeader3rdPage).isEqualTo("</api/v1/ads/pages/1>; rel=\"previous\"");

        // navigate to previous
        String previousLink = extractLinks(linkHeader3rdPage).get(0);
        assertThat(performGetRequest(previousLink).getHeader(HttpHeaders.LINK)).isEqualTo(linkHeader2ndPage);
    }

    private MockHttpServletResponse performGetRequest(String path) throws Exception {
        return mockMvc.perform(get(path)).andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON)).andReturn().getResponse();
    }

    private MockHttpServletRequestBuilder buildGetByPageRequest(int pageId) {
        return get(AdvertisementController.PATH_PAGES + pageId);
    }

    private MockHttpServletRequestBuilder buildPostRequest(String adsTitle) throws Exception {
        AdvertisementDto advertisement = createAdvertisement(adsTitle);
        return post(AdvertisementController.PATH).content(toJson(advertisement)).contentType(APPLICATION_JSON);
    }

    private String performPostAndGetId() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(buildPostRequest(SOME_TITLE)).andExpect(status().isCreated())
                .andReturn().getResponse();
        return getIdFromLocation(response.getHeader(LOCATION));
    }

    private MockHttpServletRequestBuilder buildGetRequest(String id) {
        return get(AdvertisementController.PATH + "/" + id);
    }

    private MockHttpServletRequestBuilder buildPutRequest(String id, AdvertisementDto advertisement) throws Exception {
        return put(AdvertisementController.PATH + "/" + id).content(toJson(advertisement))
                .contentType(APPLICATION_JSON);
    }

    private MockHttpServletRequestBuilder buildDeleteRequest(String id) {
        return delete(AdvertisementController.PATH + "/" + id);
    }

    private MockHttpServletRequestBuilder buildDeleteAllRequest() {
        return delete(AdvertisementController.PATH);
    }

    private static List<String> extractLinks(final String linkHeader) {
        final List<String> links = new ArrayList<>();
        Pattern pattern = Pattern.compile("<(?<link>\\S+)>");
        final Matcher matcher = pattern.matcher(linkHeader);
        while (matcher.find()) {
            links.add(matcher.group("link"));
        }
        return links;
    }

    private String toJson(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

    private String getIdFromLocation(String location) {
        return location.substring(location.lastIndexOf('/') + 1);
    }

    private <T> T convertJsonContent(MockHttpServletResponse response, Class<T> clazz) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String contentString = response.getContentAsString();
        return objectMapper.readValue(contentString, clazz);
    }

    private AdvertisementDto createAdvertisement(String title) {
        return createAdvertisement(title, "mister.x@acme.com");
    }

    private AdvertisementDto createAdvertisement(String title, String contact) {
        AdvertisementDto dto = new AdvertisementDto();
        dto.title = title;
        dto.contact = contact;
        dto.price = new BigDecimal("42.42");
        dto.currency = "EUR";
        return dto;
    }
}
