package com.darwin.simplestore.controllers;

import com.darwin.simplestore.dto.ImageDto;
import com.darwin.simplestore.dto.NewImageDto;
import com.darwin.simplestore.services.ImageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("dev")
@WebMvcTest(controllers = ImageController.class)
public class ImageControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ImageService imageService;

    private ImageDto imageDto;

    @BeforeEach
    void setUp() {
        imageDto = new ImageDto(
                1L,
                "base64"
        );
    }

    @Test
    public void testAddImage() throws Exception {
        when(imageService.addImage(any(NewImageDto.class))).thenReturn(imageDto);

        mvc.perform(post("/images")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new NewImageDto("base64"))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/images/1")))
                .andExpect(content().string(objectMapper.writeValueAsString(imageDto)));
    }

    @Test
    public void testGetImage() throws Exception {
        when(imageService.getImage(any())).thenReturn(imageDto);

        mvc.perform(get("/images/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(imageDto)));
    }

    @Test
    public void testUpdateImage() throws Exception {
        mvc.perform(put("/images/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new NewImageDto("base64"))))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteImage() throws Exception {
        mvc.perform(delete("/images/1"))
                .andExpect(status().isOk());
    }
}
