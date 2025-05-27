package ckollmeier.de.backend.controller;

import ckollmeier.de.backend.dto.FileInfoDTO;
import ckollmeier.de.backend.dto.FilesDTO;
import ckollmeier.de.backend.service.FilesService;
import ckollmeier.de.backend.service.ImagesService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FilesController.class)
class FilesControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    FilesService filesService;

    @MockitoBean
    ImagesService imagesService;

    @Nested
    @DisplayName("POST /api/files/upload")
    class UploadFileTest {

        @Test
        void upload_valid_file_returns_url() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "test.png", "image/png", new byte[]{42, 43});

            Mockito.when(filesService.saveFile(any())).thenReturn("1234");

            mockMvc.perform(multipart("/api/files/upload")
                            .file(file))
                    .andExpect(status().isOk())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("/api/files/1234")));
        }

        @Test
        void upload_empty_file_returns_bad_request() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "", "image/png", new byte[0]);

            mockMvc.perform(multipart("/api/files/upload")
                            .file(file))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Keine Datei ausgewählt")));
        }
    }

    @Nested
    @DisplayName("GET /api/files/{id}")
    class GetFileTest {

        @Test
        void get_file_by_id_returns_data_and_content_type() throws Exception {
            byte[] fakeData = "FAKE".getBytes();
            Mockito.when(filesService.getFilesDTOById("1234"))
                    .thenReturn(new FilesDTO(fakeData, "image/png", "1234.png"));

            mockMvc.perform(get("/api/files/1234"))
                    .andExpect(status().isOk())
                    .andExpect(content().bytes(fakeData))
                    .andExpect(content().contentType("image/png"));
        }

        @Test
        void get_file_by_id_with_size_calls_cropping() throws Exception {
            byte[] cropped = "cropped".getBytes();
            Mockito.when(imagesService.getCroppedImage("999", 200, "png"))
                    .thenReturn(new FilesDTO(cropped, "image/png", "999.png"));

            mockMvc.perform(get("/api/files/999")
                            .param("size", "200"))
                    .andExpect(status().isOk())
                    .andExpect(content().bytes(cropped))
                    .andExpect(content().contentType("image/png"));
        }
    }

    @Nested
    @DisplayName("GET /api/files/image/{id}/{size}")
    class GetCroppedDynamicTest {
        @Test
        void get_cropped_image_with_explicit_format() throws Exception {
            byte[] cropped = "webpdata".getBytes();
            Mockito.when(imagesService.getCroppedImage("5", 100, "webp"))
                    .thenReturn(new FilesDTO(cropped, "image/webp", "5.webp"));

            mockMvc.perform(get("/api/files/image/5/100")
                            .param("format", "webp"))
                    .andExpect(status().isOk())
                    .andExpect(content().bytes(cropped))
                    .andExpect(header().string("Content-Type", "image/webp"));
        }
        @Test
        void get_cropped_image_accepts_webp_header() throws Exception {
            byte[] cropped = "webpdat".getBytes();
            Mockito.when(imagesService.getCroppedImage("6", 77, "webp"))
                    .thenReturn(new FilesDTO(cropped, "image/webp", "6.webp"));

            mockMvc.perform(get("/api/files/image/6/77")
                            .header("Accept", "image/webp"))
                    .andExpect(status().isOk())
                    .andExpect(content().bytes(cropped))
                    .andExpect(header().string("Content-Type", "image/webp"));
        }
    }

    @Test
    @DisplayName("GET /api/files liefert alle Datei-Infos")
    void get_all_images_returns_json_list() throws Exception {
        FileInfoDTO dto = new FileInfoDTO("id1", "test.png", "image/png", "/api/files/id1");
        Mockito.when(filesService.getAllImages()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/files"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("id1"))
                .andExpect(jsonPath("$[0].contentType").value("image/png"))
                .andExpect(jsonPath("$[0].fileName").value("test.png"))
                .andExpect(jsonPath("$[0].uri").value("/api/files/id1"));
    }
}