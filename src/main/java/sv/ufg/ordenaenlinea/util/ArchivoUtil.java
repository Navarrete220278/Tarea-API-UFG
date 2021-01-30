package sv.ufg.ordenaenlinea.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.apache.http.entity.ContentType.*;

@Component
public class ArchivoUtil {
    public String obtenerNuevoNombreArchivo(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        String filename = String.format("%s.%s", UUID.randomUUID().toString(), fileExtension);
        return filename;
    }

    public Map<String, String> extraerMetadata(MultipartFile archivo) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", archivo.getContentType());
        metadata.put("Content-Length", String.valueOf(archivo.getContentType()));
        return metadata;
    }

    public void esImagen(MultipartFile archivo) throws IllegalArgumentException {
        String tipoContenido = archivo.getContentType();
        if (!Arrays.asList(IMAGE_JPEG.getMimeType(), IMAGE_PNG.getMimeType(), IMAGE_GIF.getMimeType())
                .contains(tipoContenido)) {
            throw new IllegalArgumentException(String.format("El archivo debe ser una imagen [%s]", tipoContenido));
        }
    }

    public void esImagenVacia(MultipartFile archivo) throws IllegalArgumentException {
        if (archivo.isEmpty()) {
            throw new IllegalArgumentException(
                    String.format("No se puede subir un archivo vacio [%d]",
                            archivo.getSize())
            );
        }
    }
}
