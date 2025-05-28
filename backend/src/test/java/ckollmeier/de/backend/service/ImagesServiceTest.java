package ckollmeier.de.backend.service;

import ckollmeier.de.backend.dto.FilesDTO;
import ckollmeier.de.backend.exceptions.NotFoundException;
import ckollmeier.de.backend.exceptions.ReadFilesException;
import ckollmeier.de.backend.exceptions.WriteFilesException;
import ckollmeier.de.backend.repository.FilesRepository;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

import javax.imageio.ImageIO;

class ImagesServiceTest {
    @Mock
    private GridFsTemplate gridFsTemplate;
    @Mock
    private FilesRepository filesRepository;
    @InjectMocks
    private ImagesService imagesService;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    @DisplayName("Gibt erfolgreich ein gecropptes PNG-Bild zurück")
    void getCroppedImage_success_png() throws IOException {
        String imageId = "42";
        int size = 100;
        String format = "png";

        GridFSFile file = mock(GridFSFile.class);
        when(file.getMetadata()).thenReturn(new Document("contentType", "image/png"));
        when(filesRepository.getFileById(imageId)).thenReturn(Optional.of(file));

        BufferedImage testImage = new BufferedImage(200, 100, BufferedImage.TYPE_INT_ARGB);
        GridFsResource resource = mock(GridFsResource.class);

        // ImageIO.read braucht ein richtiges Bild im InputStream. Um es zu umgehen, mocken wir ImageIO.
        when(gridFsTemplate.getResource(file)).thenReturn(resource);
        when(resource.getInputStream()).thenReturn(mock(InputStream.class)); // Inhalt wird von ImageIO direkt gemockt

        try (var mocked = org.mockito.Mockito.mockStatic(ImageIO.class)) {
            mocked.when(() -> ImageIO.read(any(InputStream.class))).thenReturn(testImage);
            mocked.when(() -> ImageIO.write(any(), eq(format), any(java.io.OutputStream.class))).thenReturn(true);

            FilesDTO dto = imagesService.getCroppedImage(imageId, size, format);

            assertNotNull(dto);
            assertEquals("image/png", dto.getContentType());
            assertEquals(resource.getFilename(), dto.getFileName());
        }
    }

    @Test
    @DisplayName("Gibt Fehler zurück, wenn das Bild nicht gefunden wird")
    void getCroppedImage_notFound() {
        String imageId = "notfound";
        int size = 100;
        String format = "png";
        when(filesRepository.getFileById(imageId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> imagesService.getCroppedImage(imageId, size, format));
    }

    @Test
    @DisplayName("Gibt Fehler zurück, wenn das Eingabebild null ist")
    void getCroppedImage_imageNull() throws IOException {
        String imageId = "test";
        int size = 100;
        String format = "png";

        GridFSFile file = mock(GridFSFile.class);
        when(filesRepository.getFileById(imageId)).thenReturn(Optional.of(file));
        GridFsResource resource = mock(GridFsResource.class);

        when(gridFsTemplate.getResource(file)).thenReturn(resource);
        when(resource.getInputStream()).thenReturn(mock(InputStream.class));
        try (var mocked = org.mockito.Mockito.mockStatic(ImageIO.class)) {
            mocked.when(() -> ImageIO.read(any(InputStream.class))).thenReturn(null);

            assertThrows(ReadFilesException.class, () -> imagesService.getCroppedImage(imageId, size, format));
        }
    }

    @Test
    @DisplayName("Gibt Fehler zurück, wenn ImageIO.write kein passendes Writer findet")
    void getCroppedImage_unsupportedFormat() throws IOException {
        String imageId = "test";
        int size = 100;
        String format = "notexists";

        GridFSFile file = mock(GridFSFile.class);
        when(filesRepository.getFileById(imageId)).thenReturn(Optional.of(file));
        BufferedImage image = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
        GridFsResource resource = mock(GridFsResource.class);

        when(gridFsTemplate.getResource(file)).thenReturn(resource);
        when(resource.getInputStream()).thenReturn(mock(InputStream.class));

        try (var mocked = org.mockito.Mockito.mockStatic(ImageIO.class)) {
            mocked.when(() -> ImageIO.read(any(InputStream.class))).thenReturn(image);
            mocked.when(() -> ImageIO.write(any(), eq(format), any(java.io.OutputStream.class))).thenReturn(false);

            assertThrows(WriteFilesException.class, () -> imagesService.getCroppedImage(imageId, size, format));
        }
    }

    @Test
    @DisplayName("Wirft ReadFilesException bei IO Fehler")
    void getCroppedImage_ioException() throws IOException {
        String imageId = "murp";
        int size = 100;
        String format = "png";
        GridFSFile file = mock(GridFSFile.class);

        when(filesRepository.getFileById(imageId)).thenReturn(Optional.of(file));
        GridFsResource resource = mock(GridFsResource.class);
        when(gridFsTemplate.getResource(file)).thenReturn(resource);
        when(resource.getInputStream()).thenThrow(new IOException("IO failed"));

        assertThrows(ReadFilesException.class, () -> imagesService.getCroppedImage(imageId, size, format));
    }
}