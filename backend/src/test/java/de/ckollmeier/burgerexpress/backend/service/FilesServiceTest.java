package de.ckollmeier.burgerexpress.backend.service;

import de.ckollmeier.burgerexpress.backend.dto.FilesDTO;
import de.ckollmeier.burgerexpress.backend.exceptions.NotFoundException;
import de.ckollmeier.burgerexpress.backend.exceptions.ReadFilesException;
import de.ckollmeier.burgerexpress.backend.exceptions.WriteFilesException;
import de.ckollmeier.burgerexpress.backend.repository.FilesRepository;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilesServiceTest {

    @Mock
    private FilesRepository filesRepository;
    @Mock
    private GridFsTemplate gridFsTemplate;

    @InjectMocks
    private FilesService filesService;

    @Nested
    @DisplayName("Tests für saveFile")
    class SaveFileTests {

        @Test
        @DisplayName("Sollte Datei erfolgreich speichern")
        void saveFile_shouldSaveFileSuccessfully_whenFileIsValid() throws IOException {
            // given
            MultipartFile file = mock(MultipartFile.class);
            when(filesRepository.saveFile(file)).thenReturn("12345");

            // when
            String result = filesService.saveFile(file);

            // then
            assertEquals("12345", result);
            verify(filesRepository, times(1)).saveFile(file);
        }

        @Test
        @DisplayName("Sollte WriteFilesException werfen bei IOException")
        void saveFile_shouldThrowWriteFilesException_whenIOExceptionOccurs() throws IOException {
            // given
            MultipartFile file = mock(MultipartFile.class);
            when(file.getOriginalFilename()).thenReturn("testfile.txt");
            when(filesRepository.saveFile(file)).thenThrow(new IOException("Speicherfehler"));

            // when / then
            WriteFilesException exception = assertThrows(
                    WriteFilesException.class,
                    () -> filesService.saveFile(file)
            );

            assertTrue(exception.getMessage().contains("Fehler beim Schreiben der Datei testfile.txt"));
        }
    }

    @Test
    @DisplayName("Wirft WriteFilesException bei IOException")
    void saveFile_throwsException() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.txt");
        when(filesRepository.saveFile(file)).thenThrow(new IOException("Fehler"));

        WriteFilesException ex = assertThrows(
                WriteFilesException.class,
                () -> filesService.saveFile(file)
        );
        assertTrue(ex.getMessage().contains("Fehler beim Schreiben der Datei test.txt"));
    }

    @Test
    @DisplayName("Lädt ein FilesDTO erfolgreich anhand einer ID, inkl. Content-Type")
    void getFilesDTOById_success_withContentType() throws IOException {
        String fileId = "abcde";
        byte[] data = "hallowelt".getBytes();
        String contentType = "text/plain";
        String fileName = "foo.txt";

        Document meta = new Document("contentType", contentType);
        GridFSFile gridFSFile = mock(GridFSFile.class);
        when(gridFSFile.getMetadata()).thenReturn(meta);
        when(gridFSFile.getFilename()).thenReturn(fileName);

        when(filesRepository.getFileById(fileId)).thenReturn(Optional.of(gridFSFile));

        InputStream inputStream = new ByteArrayInputStream(data);
        GridFsResource resource = mock(GridFsResource.class);
        when(resource.getInputStream()).thenReturn(inputStream);
        when(gridFsTemplate.getResource(gridFSFile)).thenReturn(resource);

        FilesDTO result = filesService.getFilesDTOById(fileId);

        assertArrayEquals(data, result.getData());
        assertEquals(contentType, result.getContentType());
        assertEquals(fileName, result.getFileName());
    }

    @Test
    @DisplayName("Lädt ein FilesDTO erfolgreich ohne Content-Type")
    void getFilesDTOById_success_withoutContentType() throws IOException {
        String fileId = "abcde";
        byte[] data = {1,2,3,4};
        String fileName = "foo.bin";

        GridFSFile gridFSFile = mock(GridFSFile.class);
        when(gridFSFile.getMetadata()).thenReturn(null);
        when(gridFSFile.getFilename()).thenReturn(fileName);

        when(filesRepository.getFileById(fileId)).thenReturn(Optional.of(gridFSFile));
        InputStream inputStream = new ByteArrayInputStream(data);
        GridFsResource resource = mock(GridFsResource.class);
        when(resource.getInputStream()).thenReturn(inputStream);
        when(gridFsTemplate.getResource(gridFSFile)).thenReturn(resource);

        FilesDTO result = filesService.getFilesDTOById(fileId);

        assertArrayEquals(data, result.getData());
        assertEquals("application/octet-stream", result.getContentType());
        assertEquals(fileName, result.getFileName());
    }

    @Test
    @DisplayName("Wirft NotFoundException, wenn Datei nicht existiert")
    void getFilesDTOById_notFound() {
        String fileId = "doesnotexist";
        when(filesRepository.getFileById(fileId)).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> filesService.getFilesDTOById(fileId)
        );
    }

    @Test
    @DisplayName("Wirft ReadFilesException bei IOException")
    void getFilesDTOById_ioException() throws IOException {
        String fileId = "abcde";
        GridFSFile gridFSFile = mock(GridFSFile.class);
        when(gridFSFile.getMetadata()).thenReturn(null);
        when(filesRepository.getFileById(fileId)).thenReturn(Optional.of(gridFSFile));

        GridFsResource resource = mock(GridFsResource.class);
        when(resource.getInputStream()).thenThrow(new IOException("Lesefehler"));
        when(gridFsTemplate.getResource(gridFSFile)).thenReturn(resource);

        assertThrows(
                ReadFilesException.class,
                () -> filesService.getFilesDTOById(fileId)
        );
    }

    @Test
    @DisplayName("Löscht Datei anhand ID")
    void deleteFile_success() {
        String fileId = "12";
        filesService.deleteFile(fileId);
        verify(filesRepository, times(1)).deleteFile(fileId);
    }

    @Test
    @DisplayName("Löscht Datei anhand URI")
    void deleteFileByUri_success() {
        String fileId = "4711";
        String uri = "http://localhost/files/" + fileId;

        filesService.deleteFileByUri(uri);

        verify(filesRepository, times(1)).deleteFile(fileId);
    }
}