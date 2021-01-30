package sv.ufg.ordenaenlinea.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sv.ufg.ordenaenlinea.model.Categoria;
import sv.ufg.ordenaenlinea.repository.ArchivoRepository;
import sv.ufg.ordenaenlinea.repository.CategoriaRepository;
import sv.ufg.ordenaenlinea.util.ArchivoUtil;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;
    private final Logger logger = LoggerFactory.getLogger(CategoriaService.class);
    private final String carpeta = "categoria";
    private final ArchivoRepository archivoRepository;
    private final ArchivoUtil archivoUtil;

    public Page<Categoria> obtenerCategorias(Integer page, Integer size) {
        Pageable pagina = PageRequest.of(page, size);
        Page<Categoria> categorias = categoriaRepository.findAll(pagina);
        return categorias;
    }

    public Categoria crearCategoria(Categoria categoria) throws EntityExistsException {
        lanzarExcepcionSiNombreDuplicado(categoria.getNombre());

        // Si el cliente provee una URL de imagen, ignorar el cambio, ya que se
        // debe invocar subirImagenCategoria por separado
        if (categoria.getUrlImagen() != null)
            categoria.setUrlImagen(null);

        return categoriaRepository.save(categoria);
    }

    public Categoria actualizarCategoria(Integer idCategoria, Categoria nuevaCategoria)
            throws EntityNotFoundException, EntityExistsException {

        // Obtener categoria actual de la BD
        Categoria categoria = obtenerCategoriaOLanzarExcepcion(idCategoria);

        // Si el usuario no esta modificando el nombre, no realizar ninguna accion
        if (Objects.equals(categoria.getNombre(), nuevaCategoria.getNombre())) return categoria;

        // Verificar que no exista una categoria con el mismo nombre en la BD
        lanzarExcepcionSiNombreDuplicado(nuevaCategoria.getNombre());
        categoria.setNombre(nuevaCategoria.getNombre());

        return categoriaRepository.save(categoria);
    }

    public void borrarCategoria(Integer idCategoria)
            throws EntityNotFoundException, IOException {
        // Obtener categoria actual de la BD
        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("La categoría con id %s no existe", idCategoria)
                ));

        // Si la categoria tiene una imagen, borrarla del bucket, para prevenir imagenes huérfanas
        if (categoria.getUrlImagen() != null)
            archivoRepository.borrar(carpeta, categoria.getUrlImagen());

        categoriaRepository.deleteById(idCategoria);
    }

    public void actualizarImagenCategoria(Integer idCategoria, MultipartFile archivo)
            throws IllegalArgumentException, IOException {
        // 1. Comprobar que la imagen no este vacia
        archivoUtil.esImagenVacia(archivo);

        // 2. If file is an image
        archivoUtil.esImagen(archivo);

        // 3. Grab some metadata from file
        Map<String, String> metadata = archivoUtil.extraerMetadata(archivo);

        // 4. The category exists in our database
        Categoria categoria = obtenerCategoriaOLanzarExcepcion(idCategoria);

        // 5. Store the image in S3 and return the new s3 image link
        String urlNueva = subirOReemplazarImagen(categoria.getUrlImagen(), archivo, metadata);

        // 6. Update the image URL
        categoria.setUrlImagen(urlNueva);
        categoriaRepository.save(categoria);
    }

    public byte[] obtenerImagenCategoria(Integer idCategoria) throws IOException {
        Categoria categoria = obtenerCategoriaOLanzarExcepcion(idCategoria);
        String urlImagen = categoria.getUrlImagen();

        // Si el campo esta vacio, devolver un arreglo de bytes vacio
        if (urlImagen == null || urlImagen.isBlank())
            return new byte[0];

        return archivoRepository.descargar(carpeta, urlImagen);
    }

    public void borrarImagenCategoria(Integer idCategoria) throws IOException {
        Categoria categoria = obtenerCategoriaOLanzarExcepcion(idCategoria);
        String urlImagen = categoria.getUrlImagen();

        // Si el campo esta vacio, no realizar ninguna accion
        if (urlImagen == null || urlImagen.isBlank())
            return;

        archivoRepository.borrar(carpeta, urlImagen);
        categoria.setUrlImagen(null);
        categoriaRepository.save(categoria);
    }

    private String subirOReemplazarImagen(String urlAnterior,
                                          MultipartFile archivo,
                                          Map<String, String> metadata) throws IOException {

        String urlNueva = archivoUtil.obtenerNuevoNombreArchivo(archivo);

        archivoRepository.subir(carpeta, urlNueva, Optional.of(metadata), archivo.getInputStream());

        if (urlAnterior != null)
            archivoRepository.borrar(carpeta, urlAnterior);

        return urlNueva;
    }

    private void lanzarExcepcionSiNombreDuplicado(String nombreCategoria) throws EntityExistsException {
        Optional<Categoria> categoriaHomonima = categoriaRepository.findByNombre(nombreCategoria);
        if (categoriaHomonima.isPresent())
            throw new EntityExistsException(String.format("La categoría '%s' ya existe", nombreCategoria));
    }

    private Categoria obtenerCategoriaOLanzarExcepcion(Integer idCategoria) throws EntityNotFoundException {
        return categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Categoria %s no existe", idCategoria)));
    }
}
