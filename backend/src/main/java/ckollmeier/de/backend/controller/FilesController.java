package ckollmeier.de.backend.controller;

import ckollmeier.de.backend.dto.FileInfoDTO;
import ckollmeier.de.backend.dto.FilesDTO;
import ckollmeier.de.backend.service.FilesService;
import ckollmeier.de.backend.service.ImagesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * REST-Controller für Datei- und Bildoperationen.
 * Bietet Endpunkte für das Hochladen, Abrufen, dynamisches Zuschneiden und Listen von Bilddateien.
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FilesController {

    /**
     * Service für die Verwaltung von Dateien.
     */
    private final FilesService filesService;
    /**
     * Service für Bildverarbeitung.
     */
    private final ImagesService imagesService;

    /**
     * Lädt eine Datei hoch und gibt die Zugriffs-URL als String zurück.
     *
     * @param file Die hochzuladende Multipart-Datei.
     * @return ResponseEntity mit der URL der hochgeladenen Datei oder einem Fehlerhinweis.
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(final @RequestParam("file") MultipartFile file) {
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            return ResponseEntity.badRequest().body("Keine Datei ausgewählt");
        }
        String id = filesService.saveFile(file);

        String url = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/api/files/")
                    .path(id)
                    .toUriString();

        return ResponseEntity.ok(url);
    }

    /**
     * Gibt die ungeänderte Datei anhand der ID zurück.
     * Optional kann die Übergabe eines Parameters erfolgen, um die Bildgröße zu ändern.
     *
     * @param id     Die ID der Datei.
     * @param size   Optionale gewünschte Größe des Bildausschnitts (Standard 0 = original).
     * @param format Optionales gewünschtes Bildformat wie "jpg", "png", "webp".
     * @param accept Optionaler HTTP-Header zur Auswahl des gewünschten Rückgabeformats.
     * @return ResponseEntity mit den Binärdaten und Content-Type des Bildes.
     */
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getFileById(
            final @PathVariable String id,
            final @RequestParam(defaultValue = "0") int size,
            final @RequestParam(required = false) String format,
            final @RequestHeader(value = "Accept", required = false) String accept

    ) {
        if (size > 0) {
            return getCroppedImageDynamic(id, size, format, accept);
        }
        FilesDTO filesDTO = filesService.getFilesDTOById(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(filesDTO.getContentType())).body(filesDTO.getData());
    }

    /**
     * Gibt ein dynamisch zugeschnittenes Bild in angeforderter Größe und Format zurück.
     *
     * @param id     Die ID der Bilddatei.
     * @param size   Die gewünschte Größe (Breite und Höhe) in Pixeln.
     * @param format Optionales gewünschtes Bildformat.
     * @param accept Optionaler Accept-Header zur Formatbestimmung.
     * @return ResponseEntity mit den Bilddaten im gewünschten/zugelassenen Format.
     */
    @GetMapping("/image/{id}/{size}")
    public ResponseEntity<byte[]> getCroppedImageDynamic(
            final @PathVariable String id,
            final @PathVariable int size,
            final @RequestParam(required = false) String format,
            final @RequestHeader(value = "Accept", required = false) String accept
    ) {
        // 🎯 Format bestimmen
        String chosenFormat = "png"; // default
        if (format != null) {
            chosenFormat = format.toLowerCase();
        } else if (accept != null && accept.toLowerCase().contains("image/webp")) {
            chosenFormat = "webp";
        }

        FilesDTO image = imagesService.getCroppedImage(id, size, chosenFormat);

        MediaType mediaType = switch (chosenFormat) {
            case "webp" -> MediaType.valueOf("image/webp");
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            default -> MediaType.IMAGE_PNG;
        };

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setCacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic());

        return new ResponseEntity<>(image.getData(), headers, HttpStatus.OK);
    }

    /**
     * Gibt eine Liste aller gespeicherten Bilddateien zurück.
     *
     * @return ResponseEntity mit einer Liste aller Bild-DTOs im JSON-Format.
     */
    @GetMapping()
    public ResponseEntity<List<FileInfoDTO>> getAllImages() {
        List<FileInfoDTO> filesDTO = filesService.getAllImages();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(filesDTO);
    }
}