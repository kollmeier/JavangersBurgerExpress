package de.ckollmeier.burgerexpress.backend.repository;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Repository zum Verwalten von Dateien im MongoDB GridFS.
 * Bietet Methoden zum Speichern, Abrufen und Löschen von Dateien.
 */
@Repository
@RequiredArgsConstructor
public class FilesRepository {
    /**
     * Instanz von {@link GridFsTemplate}, die für Operationen mit MongoDB GridFS verwendet wird.
     * Bietet Methoden zum Speichern, Abrufen und Löschen von Dateien in GridFS.
     * Diese Instanz wird über Dependency Injection bereitgestellt.
     */
    private final GridFsTemplate gridFsTemplate;

    /**
     * Sucht und liefert eine Datei im GridFS anhand der Id.
     *
     * @param id Die Id der Datei (hexadezimaler String)
     * @return Optional mit der GridFSFile falls gefunden, sonst leer
     */
    public Optional<GridFSFile> getFileById(final String id) {
        //noinspection OptionalOfNullableMisuse
        return Optional.ofNullable(gridFsTemplate.findOne(query(where("_id").is(new ObjectId(id)))));
    }

    /**
     * Speichert eine Datei im GridFS. 
     * Es werden zusätzliche Metadaten wie Content-Type, Dateigröße, Dateiname und Upload-Datum gespeichert.
     *
     * @param file Das zu speichernde MultipartFile
     * @return Die Id der gespeicherten Datei als hexadezimaler String
     * @throws IOException Falls beim Zugriff auf den Datei-Stream ein Fehler auftritt
     */
    public String saveFile(final MultipartFile file) throws IOException {
        DBObject metaData = new BasicDBObject();
        metaData.put("contentType", file.getContentType());
        metaData.put("size", file.getSize());
        metaData.put("originalFilename", file.getOriginalFilename());
        metaData.put("uploadDate", new java.util.Date());

        ObjectId id = gridFsTemplate.store(
                file.getInputStream(),
                file.getOriginalFilename(),
                file.getContentType(),
                metaData
        );

        return id.toHexString();
    }

    /**
     * Löscht eine Datei aus dem GridFS anhand der Id.
     *
     * @param id Die Id der zu löschenden Datei (hexadezimaler String)
     */
    public void deleteFile(final String id) {
        gridFsTemplate.delete(query(where("_id").is(new ObjectId(id))));
    }

    public void deleteAllFiles() {
        gridFsTemplate.delete(query(where("metadata.contentType").exists(true)));
    }

    /**
     * Retrieves all files from the MongoDB GridFS whose content type partially matches the specified string.
     * The matching is case-insensitive and looks for content types that start with the provided partial content type.
     *
     * @param contentType The partial content type string to search for. The search is case-insensitive and matches
     *                    content types that begin with this string.
     * @return An {@code Iterable} of {@code GridFSFile} objects that match the specified partial content type.
     */
    public Iterable<GridFSFile> getAllFilesByPartialContentType(final String contentType) {
        return gridFsTemplate.find(query(where("metadata.contentType").regex("^" + contentType, "i")));
    }

    /**
     * Retrieves all images stored in the MongoDB GridFS. Images are identified by having a content type
     * that begins with "image/" (case-insensitive).
     *
     * @return An {@code Iterable} of {@code GridFSFile} objects representing all image files available in GridFS.
     */
    public Iterable<GridFSFile> getAllImages() {
        return getAllFilesByPartialContentType("image/");
    }
}