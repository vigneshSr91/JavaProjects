package com.sap.cc.bulletinboard.ads;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AdControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AdvertisementRepository advertisementStorage;

    @BeforeEach
    public void beforeEach(){
        advertisementStorage.deleteAll();
    }
    @Test
    public void getAds_initially_returnsEmptyList() throws Exception {
        this.mockMvc.perform(get("/api/v1/ads"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[]")));
    }

    @Test
    public void postAd_returnsCreatedAdvertisement() throws Exception {
        Advertisement newAd = new Advertisement();
        String jsonBody = objectMapper.writeValueAsString(newAd);

        MockHttpServletResponse response =
                this.mockMvc.perform(post("/api/v1/ads")
                                .content(jsonBody)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andReturn().getResponse();

        String responseInJSON = response.getContentAsString();
        Advertisement createdAd = objectMapper.readValue(responseInJSON, Advertisement.class);

        assertThat(createdAd.getId(), notNullValue());
    }

    @Test
    public void postAd_and_getSingle_returnsTheAd() throws Exception{
        Advertisement newAd= new Advertisement();
        newAd.setContact("ok@example.com");
        String jsonBody = objectMapper.writeValueAsString(newAd);

        MockHttpServletResponse response =
                this.mockMvc.perform(post("/api/v1/ads")
                                .content(jsonBody)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();

        response =
                this.mockMvc.perform(get(response.getHeader("location")))
                        .andExpect(status().isOk())
                        .andReturn().getResponse();

        String responseInJSON = response.getContentAsString();
        Advertisement createdAd = objectMapper.readValue(responseInJSON, Advertisement.class);

        assertThat(createdAd.getId(), notNullValue());
    }

    @Test
    public void getSingleWithNegativeId_raisesIllegalArgumentException() throws Exception{

        this.mockMvc.perform(get("/api/v1/ads/-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getSingle_idNotExisting_returnsNotFound() throws Exception{
        this.mockMvc.perform(get("/api/v1/ads/111"))
                .andExpect(status().isNotFound());
    }
}
