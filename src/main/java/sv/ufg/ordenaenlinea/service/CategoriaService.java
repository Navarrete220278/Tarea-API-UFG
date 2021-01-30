package sv.ufg.ordenaenlinea.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sv.ufg.ordenaenlinea.model.Categoria;
import sv.ufg.ordenaenlinea.repository.CategoriaRepository;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;
import java.io.IOException;
import java.util.*;

import static org.apache.http.entity.ContentType.*;

@Service
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;
    private final Logger logger = LoggerFactory.getLogger(CategoriaService.class);
    private final String carpeta = "categoria";
    private final ArchivoService archivoService;

    @Autowired
    public CategoriaService(CategoriaRepository categoriaRepository, ArchivoService archivoService) {
        this.categoriaRepository = categoriaRepository;
        this.archivoService = archivoService;
    }

    public List<Categoria> obtenerCategorias() {
        return categoriaRepository.findAll();
    }

    public Categoria agregarCategoria(Categoria categoria) throws EntityExistsException {
        lanzarExcepcionSiNombreDuplicado(categoria);

        // Si el cliente provee una URL de imagen, ignorar el cambio, ya que se
        // debe invocar subirImagenCategoria por separado
        if (categoria.getUrlImagen() != null)
            categoria.setUrlImagen(null);

        return categoriaRepository.save(categoria);
    }

    public Categoria modificarCategoria(Integer idCategoria, Categoria categoria)
            throws EntityNotFoundException, EntityExistsException, IllegalArgumentException {
        lanzarExcepcionSiIdCategoriaDifiereDeCategoria(idCategoria, categoria);
        lanzarExcepcionSiNombreDuplicado(categoria);

        // Obtener categoria actual de la BD
        Optional<Categoria> categoriaOptional = categoriaRepository.findById(idCategoria);

        // Lanzar una excepcion si no existe una categoria con este ID
        if (categoriaOptional.isEmpty())
            throw new EntityNotFoundException(String.format("La categoría con id %s no existe", idCategoria));

        // Si el usuario trata de actualizar la URL, ignorar el cambio (se debe invocar a subirImagenCategoria)
        if (!Objects.equals(categoria.getUrlImagen(), categoriaOptional.get().getUrlImagen()))
            categoria.setUrlImagen(categoriaOptional.get().getUrlImagen());

        // Guardar cambios
        return categoriaRepository.save(categoria);
    }

    public void borrarCategoria(Integer idCategoria)
            throws EntityNotFoundException, IOException {
        // Obtener categoria actual de la BD
        Optional<Categoria> categoriaOptional = categoriaRepository.findById(idCategoria);

        // Lanzar una excepcion si no existe una categoria con este ID
        if (categoriaOptional.isEmpty())
            throw new EntityNotFoundException(String.format("La categoría con id %s no existe", idCategoria));

        // Si la categoria tiene una imagen, borrarla del bucket, para prevenir imagenes huérfanas
        if (categoriaOptional.get().getUrlImagen() != null)
            archivoService.borrar(carpeta, categoriaOptional.get().getUrlImagen());

        categoriaRepository.deleteById(idCategoria);
    }

    public void subirImagenCategoria(Integer idCategoria, MultipartFile archivo)
            throws IllegalArgumentException, IOException {
        // 1. Comprobar que la imagen no este vacia
        esImagenVacia(archivo);

        // 2. If file is an image
        esImagen(archivo);

        // 3. The user exists in our database
        Optional<Categoria> categoria = categoriaRepository.findById(idCategoria);
        if (!categoria.isPresent())
            throw new EntityNotFoundException(String.format("La categoria %s no existe", idCategoria));

        // 4. Grab some metadata from file
        Map<String, String> metadata = extraerMetadata(archivo);

        // 5. Get the current image URL
        String imagenAnterior = categoria.get().getUrlImagen();

        // 6. Store the image in S3 and update database (userProfileImageLink) with s3 image link
        String nombreArchivo = obtenerNuevoNombreArchivo(archivo);
        archivoService.subir(carpeta, nombreArchivo, Optional.of(metadata), archivo.getInputStream());
        categoria.get().setUrlImagen(nombreArchivo);
        categoriaRepository.save(categoria.get());

        // 7. Borrar la imagen anterior del bucket
        if (imagenAnterior != null)
            archivoService.borrar(carpeta, imagenAnterior);
    }

    public byte[] descargarImagenCategoria(Integer idCategoria) throws IOException {
        Categoria categoria = obtenerCategoriaOLanzarExcepcion(idCategoria);
        String urlImagen = categoria.getUrlImagen();

        // Si el campo esta vacio, devolver un arreglo de bytes vacio
        if (urlImagen == null || urlImagen.isBlank())
            return new byte[0];

        return archivoService.descargar(carpeta, urlImagen);
    }

    private void lanzarExcepcionSiIdCategoriaDifiereDeCategoria(Integer idCategoria, Categoria categoria) {
        if (!Objects.equals(idCategoria, categoria.getId()))
            throw new IllegalArgumentException(String.format("El id %s no corresponde a la categoria", idCategoria));
    }

    private void lanzarExcepcionSiNombreDuplicado(Categoria categoria) throws EntityExistsException {
        Optional<Categoria> categoriaHomonima = categoriaRepository.findCategoriaByNombre(categoria.getNombre());
        if (categoriaHomonima.isPresent())
            throw new EntityExistsException(String.format("La categoría '%s' ya existe", categoria.getNombre()));
    }

    private String obtenerNuevoNombreArchivo(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        String filename = String.format("%s.%s", UUID.randomUUID().toString(), fileExtension);
        return filename;
    }

    private Map<String, String> extraerMetadata(MultipartFile archivo) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", archivo.getContentType());
        metadata.put("Content-Length", String.valueOf(archivo.getContentType()));
        return metadata;
    }

    private void esImagen(MultipartFile archivo) throws IllegalArgumentException {
        String tipoContenido = archivo.getContentType();
        if (!Arrays.asList(IMAGE_JPEG.getMimeType(), IMAGE_PNG.getMimeType(), IMAGE_GIF.getMimeType())
                .contains(tipoContenido)) {
            throw new IllegalArgumentException(String.format("El archivo debe ser una imagen [%s]", tipoContenido));
        }
    }

    private void esImagenVacia(MultipartFile archivo) throws IllegalArgumentException {
        if (archivo.isEmpty()) {
            throw new IllegalArgumentException(
                    String.format("No se puede subir un archivo vacio [%d]",
                    archivo.getSize())
            );
        }
    }

    private Categoria obtenerCategoriaOLanzarExcepcion(Integer idCategoria) {
        return categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Categoria %s no existe", idCategoria)));
    }
}
