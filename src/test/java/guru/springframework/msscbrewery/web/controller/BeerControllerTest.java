package guru.springframework.msscbrewery.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.msscbrewery.services.BeerService;
import guru.springframework.msscbrewery.web.model.BeerDto;

import guru.springframework.msscbrewery.web.model.BeerStyle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeerController.class)
public class BeerControllerTest {

    @MockBean
    BeerService beerService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    BeerDto beerDto;

    @BeforeEach
    public void setUp() {
        beerDto = BeerDto.builder().id(UUID.randomUUID())
                .beerName("Beer 1")
                .beerStyle(BeerStyle.IPA)
                .upc(1234567890L)
                .build();
    }

    @Test
    public void getBeer() throws Exception {

        given(beerService.getBeerById(any(UUID.class))).willReturn(beerDto);

        mockMvc.perform(get("/api/v1/beer/" + beerDto.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(beerDto.getId().toString())))
                .andExpect(jsonPath("$.beerName", is("Beer 1")))
                .andExpect(jsonPath("$.beerStyle", is("IPA")))
                .andExpect(jsonPath("$.upc", is(1234567890)));
    }

    @Test
    public void handlePost() throws Exception {

        String beerDtoJson = objectMapper.writeValueAsString(beerDto);
        given(beerService.saveBeer(any(BeerDto.class))).willReturn(beerDto);

        mockMvc.perform(post("/api/v1/beer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(beerDtoJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void handleUpdate() throws Exception {

        String beerDtoJson = objectMapper.writeValueAsString(beerDto);

        mockMvc.perform(put("/api/v1/beer/" + beerDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(beerDtoJson))
                .andExpect(status().isNoContent());

        then(beerService).should().updateBeer(beerDto.getId(), beerDto);
    }

    @Test
    public void handleDelete() throws Exception {

        mockMvc.perform(delete("/api/v1/beer/" + beerDto.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        then(beerService).should().deleteBeer(beerDto.getId());
    }
}