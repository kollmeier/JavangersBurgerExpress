package ckollmeier.de.backend.service;

import ckollmeier.de.backend.dto.FileInfoDTO;
import ckollmeier.de.backend.dto.FilesDTO;
import ckollmeier.de.backend.exceptions.NotFoundException;
import ckollmeier.de.backend.exceptions.ReadFilesException;
import ckollmeier.de.backend.exceptions.WriteFilesException;
import ckollmeier.de.backend.repository.FilesRepository;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * Service für das Management von Dateien mithilfe von MongoDB GridFS.
 * Bietet Funktionen zum Speichern, Abrufen und Löschen von Dateien.
 */
@Service
@RequiredArgsConstructor
public class FilesService {
    private static final String CONTENT_TYPE = "contentType";

    /**
     * Template für das Speichern und Laden von Dateien.
     */
    private final GridFsTemplate gridFsTemplate;
    /**
     * Repository für das Speichern und Laden von Dateien.
     */
    private final FilesRepository filesRepository;

    /**
     * Speichert eine Datei im Dateispeicher.
     *
     * @param file MultipartFile, das gespeichert werden soll.
     * @return Die ID der gespeicherten Datei.
     * @throws WriteFilesException Wenn das Speichern fehlschlägt.
     */
    public String saveFile(final MultipartFile file) {
        try {
            return filesRepository.saveFile(file);
        } catch (IOException e) {
            throw new WriteFilesException(String.format("Fehler beim Schreiben der Datei %s", file.getOriginalFilename()), e);
        }
    }

    /**
     * Lädt eine Datei anhand der ID als FilesDTO.
     *
     * @param id Die ID der Datei.
     * @return Die Datei als FilesDTO.
     * @throws NotFoundException Wenn die Datei nicht gefunden wurde.
     * @throws ReadFilesException Wenn ein Fehler beim Lesen der Datei auftritt.
     */
    public FilesDTO getFilesDTOById(final String id) {
        GridFSFile file = filesRepository.getFileById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Datei %s nicht gefunden", id)));

        return getFilesDTO(file);
    }

    private FilesDTO getFilesDTO(final GridFSFile file) {
        GridFsResource resource = gridFsTemplate.getResource(file);

        String contentType = "application/octet-stream";
        if (file.getMetadata() != null && file.getMetadata().get(CONTENT_TYPE) != null) {
            contentType = file.getMetadata().get(CONTENT_TYPE).toString();
        }

        try {
            byte[] data = resource.getInputStream().readAllBytes();
            return new FilesDTO(
                    data,
                    contentType,
                    file.getFilename()
            );
        } catch (IOException e) {
            throw new ReadFilesException("Fehler beim Lesen der Datei", e);
        }
    }

    /**
     * Löscht eine Datei anhand ihrer ID.
     *
     * @param id Die ID der zu löschenden Datei.
     */
    public void deleteFile(final String id) {
        filesRepository.deleteFile(id);
    }

    /**
     * Löscht eine Datei anhand ihrer URI.
     *
     * @param uri Die URI der zu löschenden Datei.
     */
    public void deleteFileByUri(final String uri) {
        String id = uri.substring(uri.lastIndexOf('/') + 1);
        filesRepository.deleteFile(id);
    }

    /**
     * Retrieves all image files stored in the MongoDB GridFS. Images are identified by having
     * a content type that begins with "image/" (case-insensitive).
     *
     * @return An {@code Iterable} of {@code GridFSFile} objects representing all image files available in GridFS.
     */
    public List<FileInfoDTO> getAllImages() {
        Iterable<GridFSFile> files = filesRepository.getAllImages();
        return StreamSupport.stream(files.spliterator(), false)
                .map(file -> new FileInfoDTO(
                        file.getObjectId().toHexString(),
                        file.getFilename(),
                        file.getMetadata().get(CONTENT_TYPE).toString(),
                        String.format("/api/files/%s", file.getObjectId().toHexString())))
                .toList();
    }
}