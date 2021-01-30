package sv.ufg.ordenaenlinea.repository;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

@Repository
public class ArchivoRepository {
    @Value("${aws.s3.bucket.name}")
    private String bucketName;
    @Value("${aws.s3.bucket.app-folder}")
    private String appFolder;
    private final AmazonS3 s3;
    private final Logger logger = LoggerFactory.getLogger(ArchivoRepository.class);

    @Autowired
    public ArchivoRepository(AmazonS3 s3) {
        this.s3 = s3;
    }

    public void subir(String carpetaDestino,
                      String nombreArchivo,
                      Optional<Map<String, String>> metadataOpcional,
                      InputStream inputStream) throws IOException {

        ObjectMetadata metadata = new ObjectMetadata();

        metadataOpcional.ifPresent(map -> {
            if (!map.isEmpty()) {
                map.forEach(metadata::addUserMetadata);
            }
        });

        try {
            String llave = obtenerLlaveBucket(carpetaDestino, nombreArchivo);
            s3.putObject(bucketName, llave, inputStream, metadata);
        } catch (AmazonServiceException e) {
            String mensajeError = String.format("No se pudo guardar el archivo %s", nombreArchivo);
            logger.error(mensajeError, e);
            throw new IOException(mensajeError);
        }
    }

    public byte[] descargar(String carpetaDestino, String nombreArchivo) throws IOException {
        try {
            String llave = obtenerLlaveBucket(carpetaDestino, nombreArchivo);
            S3Object s3Object = s3.getObject(bucketName, llave);
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            return IOUtils.toByteArray(inputStream);
        } catch (AmazonServiceException | IOException e) {
            String mensajeError = String.format("No se pudo descargar el archivo %", nombreArchivo);
            logger.error(mensajeError, e);
            throw new IOException(mensajeError);
        }
    }

    public void borrar(String carpetaDestino, String nombreArchivo) throws IOException {
        try {
            String llave = obtenerLlaveBucket(carpetaDestino, nombreArchivo);
            // Borrar el elemento
            s3.deleteObject(bucketName, llave);
        } catch (AmazonServiceException e) {
            String mensajeError = String.format("No se pudo borrar el archivo %s", nombreArchivo);
            logger.error(mensajeError, e);
            throw new IOException(mensajeError);
        }
    }

    private String obtenerLlaveBucket(String carpetaDestino, String nombreArchivo) {
        return String.format("%s/%s/%s", appFolder, carpetaDestino, nombreArchivo);
    }
}

