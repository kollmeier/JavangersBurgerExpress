package de.ckollmeier.burgerexpress.backend.service;

import de.ckollmeier.burgerexpress.backend.dto.FilesDTO;
import de.ckollmeier.burgerexpress.backend.exceptions.NotFoundException;
import de.ckollmeier.burgerexpress.backend.exceptions.ReadFilesException;
import de.ckollmeier.burgerexpress.backend.exceptions.WriteFilesException;
import de.ckollmeier.burgerexpress.backend.repository.FilesRepository;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.twelvemonkeys.image.ResampleOp;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

// OpenCV imports
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * Service zur Arbeit mit Bilddateien.
 * Stellt Methoden bereit, um Bilder aus der Datenbank zu lesen, zu bearbeiten (z.B. Größe ändern, Format konvertieren)
 * und sie beispielsweise als WebP oder in anderen Formaten auszuliefern.
 * Nutzt GridFS (MongoDB) und unterstützt Caching für Bildoperationen.
 */
@Service
@RequiredArgsConstructor
public class ImagesService {

    /** Template für den Zugriff auf GridFS in MongoDB. */
    private final GridFsTemplate gridFsTemplate;
    /** Repository für Metadaten und Abfragen zu gespeicherten Dateien. */
    private final FilesRepository filesRepository;

    static {
        try {
            nu.pattern.OpenCV.loadLocally();
        } catch (Exception e) {
            // Fehlerbehandlung sofern notwendig
        }
    }

    /**
     * Liefert ein Bild aus der Datenbank als zugeschnittene, auf die gewünschte Zielgröße skalierte und
     * in das gewünschte Format konvertierte Kopie zurück. Das Ergebnis wird gecached.
     * Unterstützte Ausgabeformate sind u.a. PNG, JPEG und WebP.
     *
     * @param id     Die ID des Bildes in der Datenbank.
     * @param size   Zielgröße (Breite und Höhe in Pixel, Bild wird ggf. skaliert und zugeschnitten).
     * @param format Gewünschtes Bildformat, z. B. "png", "jpeg", "webp".
     * @return Ein {@link FilesDTO} mit dem fertigen Bild (konvertiert, skaliert, im gewünschten Format).
     * @throws NotFoundException    Wenn das Bild mit dieser ID nicht gefunden wurde.
     * @throws ReadFilesException   Wenn das Bild nicht gelesen, erkannt oder konvertiert werden konnte.
     * @throws WriteFilesException  Wenn keine Unterstützung für das gewünschte Ausgabeformat besteht.
     */
    @Cacheable(value = "images", key = "#id + '-' + #size + '-' + #format")
    public FilesDTO getCroppedImage(final String id, final int size, final String format) {
        GridFSFile file = filesRepository.getFileById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Bild %s nicht gefunden", id)));

        GridFsResource resource = gridFsTemplate.getResource(file);

        String contentType = "application/octet-stream";
        if (file.getMetadata() != null && file.getMetadata().get("contentType") != null) {
            contentType = file.getMetadata().get("contentType").toString();
        }

        try {
            BufferedImage originalImage = ImageIO.read(resource.getInputStream());
            if (originalImage == null) {
                throw new ReadFilesException(
                        "Original image is null, possibly due to unsupported format or invalid image data", null
                );
            }
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();
            double aspectRatio = (double) originalWidth / originalHeight;
            int newWidth;
            int newHeight;
            if (originalWidth > originalHeight) {
                newWidth = size;
                newHeight = (int) (size / aspectRatio);
            } else {
                newWidth = (int) (size * aspectRatio);
                newHeight = size;
            }
            BufferedImageOp resampler = new ResampleOp(newWidth, newHeight);
            BufferedImage resizedImage = resampler.filter(originalImage, null);

            if ("webp".equalsIgnoreCase(format)) {
                // BufferedImage zu OpenCV-Matrix konvertieren
                Mat mat = bufferedImageToMat4Channels(resizedImage);

                // In WebP kodieren
                MatOfByte buf = new MatOfByte();
                boolean result = Imgcodecs.imencode(".webp", mat, buf);
                if (!result) {
                    throw new WriteFilesException("OpenCV konnte das Bild nicht zu WebP konvertieren", null);
                }
                return new FilesDTO(
                        buf.toArray(),
                        "image/webp",
                        resource.getFilename()
                );
            } else {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                boolean imageWritten = ImageIO.write(resizedImage, format, outputStream);
                if (!imageWritten) {
                    throw new WriteFilesException("No ImageWriters found for format: " + format, null);
                }

                return new FilesDTO(
                        outputStream.toByteArray(),
                        contentType,
                        resource.getFilename()
                );
            }
        } catch (IOException e) {
            throw new ReadFilesException("Error reading file", e);
        }
    }

    /**
     * Converts a {@link BufferedImage} into an OpenCV {@link Mat} with 4 channels (BGRA).
     * This method extracts the ARGB data from the BufferedImage and transforms it into a
     * Mat representation using the OpenCV format, where the channels are ordered as Blue,
     * Green, Red, and Alpha (BGRA). The resulting matrix can be used for further image
     * processing in OpenCV.
     *
     * @param bi the input {@link BufferedImage} to be converted, assumed to have ARGB format.
     * @return a {@link Mat} object representing the converted image in BGRA format.
     */
    public Mat bufferedImageToMat4Channels(final BufferedImage bi) {
        int width = bi.getWidth();
        int height = bi.getHeight();
        Mat mat = new Mat(height, width, CvType.CV_8UC4); // 4 Kanäle (BGRA)
        int[] data = new int[width * height];
        bi.getRGB(0, 0, width, height, data, 0, width);
        byte[] bytes = new byte[width * height * 4];
        for (int i = 0; i < data.length; i++) {
            int argb = data[i];
            // OpenCV BGRA
            bytes[i * 4] = (byte) (argb & 0xFF);          // Blau
            bytes[i * 4 + 1] = (byte) ((argb >> 8) & 0xFF);   // Grün
            bytes[i * 4 + 2] = (byte) ((argb >> 16) & 0xFF);  // Rot
            bytes[i * 4 + 3] = (byte) ((argb >> 24) & 0xFF);  // Alpha
        }
        mat.put(0, 0, bytes);
        return mat;
    }
    /**
     * Hilfsklasse für das WebP-Encoding mittels OpenCV.
     */
    private static class MatOfByte extends org.opencv.core.MatOfByte {
        public MatOfByte() {
            super();
        }
    }
}