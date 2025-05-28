package de.ckollmeier.burgerexpress.backend.controller;

import de.ckollmeier.burgerexpress.backend.dto.FileInfoDTO;
import de.ckollmeier.burgerexpress.backend.repository.FilesRepository;
import de.ckollmeier.burgerexpress.backend.service.FilesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.nio.file.Files;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FilesControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    FilesService filesService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FilesRepository filesRepository;

    protected String fakeFileId;
    protected String realFileId;

    protected final byte[] fakeData = new byte[]{42, 43};
    protected byte[] realData;

    @BeforeEach
    void setUp() throws Exception{
        filesRepository.deleteAllFiles(); // Sicherstellen, dass das Repository leer ist

        File imageResource = new ClassPathResource("cheeseburger.png").getFile();
        realData = Files.readAllBytes(imageResource.toPath());

        MockMultipartFile realFile = new MockMultipartFile(
                "file", "realfile.png", "image/png", realData);
        realFileId = filesRepository.saveFile(realFile);

        MockMultipartFile fakeFile = new MockMultipartFile(
                "file", "fakefile.png", "image/png", fakeData);
        fakeFileId = filesRepository.saveFile(fakeFile);
    }

    @Nested
    @DisplayName("POST /api/files/upload")
    class UploadFileTest {
        @Test
        void upload_valid_file_returns_url() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "test.png", "image/png", new byte[]{42, 43});

            String result = mockMvc.perform(multipart("/api/files/upload")
                            .file(file))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.contentType").value("image/png"))
                    .andReturn().getResponse().getContentAsString();

            FileInfoDTO resultDto = objectMapper.readValue(result, FileInfoDTO.class);

            assertThat(resultDto)
                    .isNotNull()
                    .hasFieldOrProperty("id");

            String resultId = resultDto.id();

            assertThat(resultDto.uri()).endsWith("/api/files/" + resultId);

            assertThat(resultDto)
                    .hasFieldOrPropertyWithValue("fileName", "test.png")
                    .hasFieldOrPropertyWithValue("contentType", "image/png");

            assertThat(filesService.getFilesDTOById(resultId))
                .isNotNull()
                .hasFieldOrPropertyWithValue("fileName", "test.png")
                .hasFieldOrPropertyWithValue("contentType", "image/png")
                .hasFieldOrPropertyWithValue("data", new byte[]{42, 43});
        }

        @Test
        void upload_empty_file_returns_bad_request() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "", "image/png", new byte[0]);

            mockMvc.perform(multipart("/api/files/upload")
                            .file(file))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(Matchers.containsString("Keine Datei ausgewählt")));
        }
    }

    @Nested
    @DisplayName("GET /api/files/{id}")
    class GetFileTest {
        @Test
        void get_file_by_id_returns_data_and_content_type() throws Exception {
            mockMvc.perform(get("/api/files/" + fakeFileId))
                    .andExpect(status().isOk())
                    .andExpect(content().bytes(fakeData))
                    .andExpect(content().contentType("image/png"))
                    .andExpect(header().longValue("Content-Length", 2));
        }

        @Test
        void get_file_by_id_returns_404_response() throws Exception {
            mockMvc.perform(get("/api/files/" + new ObjectId()))
                    .andExpect(status().isNotFound());
        }

        @Test
        void get_file_by_id_with_size_calls_cropping() throws Exception {
            mockMvc.perform(get("/api/files/" + realFileId)
                            .param("size", "200"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("image/png"))
                    .andExpect(header().longValue("Content-Length", 53576)); // Content Length als Indikator, dass die Datei gecropped wurde
        }
    }

    @Nested
    @DisplayName("GET /api/files/image/{id}/{size}")
    class GetCroppedDynamicTest {
        @Test
        void get_cropped_image_with_explicit_format() throws Exception {
            mockMvc.perform(get("/api/files/" + realFileId + "/200")
                            .param("format", "webp"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Type", "image/webp"));
        }

        @Test
        void get_dynamic_file_by_id_with_size_calls_cropping() throws Exception {
            mockMvc.perform(get("/api/files/" + realFileId + "/200"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("image/png"))
                    .andExpect(header().longValue("Content-Length", 53576)); // Content Length als Indikator, dass die Datei gecropped wurde
        }

        @Test
        void get_dynamic_file_by_id_with_format_changes_format() throws Exception {
            mockMvc.perform(get("/api/files/" + realFileId + "/200")
                            .header("Accept", "image/webp"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("image/webp"));
        }
    }

    @Test
    @DisplayName("GET /api/files liefert alle Datei-Infos")
    void get_all_images_returns_json_list() throws Exception {
        mockMvc.perform(get("/api/files"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(realFileId))
                .andExpect(jsonPath("$[0].contentType").value("image/png"))
                .andExpect(jsonPath("$[0].fileName").value("realfile.png"))
                .andExpect(jsonPath("$[1].id").value(fakeFileId))
                .andExpect(jsonPath("$[1].contentType").value("image/png"))
                .andExpect(jsonPath("$[1].fileName").value("fakefile.png"));
    }

    @Test
    @DisplayName("GET /api/files liefert nur images")
    void get_all_images_returns_json_list_only_of_images() throws Exception {
        MockMultipartFile textFile = new MockMultipartFile("file", "textfile.txt", "application/txt", "Test text".getBytes());
        filesRepository.saveFile(textFile);
        mockMvc.perform(get("/api/files"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(realFileId))
                .andExpect(jsonPath("$[0].contentType").value("image/png"))
                .andExpect(jsonPath("$[0].fileName").value("realfile.png"))
                .andExpect(jsonPath("$[1].id").value(fakeFileId))
                .andExpect(jsonPath("$[1].contentType").value("image/png"))
                .andExpect(jsonPath("$[1].fileName").value("fakefile.png"));
    }
}