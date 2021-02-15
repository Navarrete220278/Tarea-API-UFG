package sv.ufg.ordenaenlinea.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sv.ufg.ordenaenlinea.model.Categoria;
import sv.ufg.ordenaenlinea.repository.ArchivoRepository;
import sv.ufg.ordenaenlinea.repository.CategoriaRepository;
import sv.ufg.ordenaenlinea.repository.ProductoRepository;
import sv.ufg.ordenaenlinea.request.CategoriaRequest;
import sv.ufg.ordenaenlinea.util.ArchivoUtil;
import sv.ufg.ordenaenlinea.util.ModificacionUtil;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final ArchivoRepository archivoRepository;
    private final ArchivoUtil archivoUtil;
    private final ModificacionUtil modificacionUtil;
    private final String CARPETA = "categoria";

    public Page<Categoria> obtenerCategorias(Pageable pageable) {
        return categoriaRepository.findAll(pageable);
    }

    public Categoria obtenerCategoriaPorId(Integer idCategoria) {
        Categoria categoria = encontrarCategoriaPorIdOLanzarExcepcion(idCategoria);
        return categoria;
    }

    public byte[] obtenerImagenCategoria(Integer idCategoria) {
        Categoria categoria = encontrarCategoriaPorIdOLanzarExcepcion(idCategoria);

        if (categoria.getUrlImagen() == null || categoria.getUrlImagen().isBlank())
            return new byte[0];
        else
            return archivoRepository.descargar(CARPETA, categoria.getUrlImagen());
    }

    public Categoria crearCategoria(CategoriaRequest categoriaRequest) {
        lanzarExcepcionSiNombreCategoriaYaExiste(categoriaRequest);

        return categoriaRepository.save(Categoria.of(categoriaRequest));
    }

    public Categoria modificarCategoria(Integer idCategoria, CategoriaRequest categoriaRequest) {
        Categoria categoria = encontrarCategoriaPorIdOLanzarExcepcion(idCategoria);

        // Si el nombre no ha sido modificado, no realizar ninguna acción
        if (!modificacionUtil.textoHaSidoModificado(categoriaRequest.getNombre(), categoria.getNombre()))
            return categoria;

        lanzarExcepcionSiNombreCategoriaYaExiste(categoriaRequest);

        categoria.setNombre(categoriaRequest.getNombre());
        return categoriaRepository.save(categoria);
    }

    public void modificarImagenCategoria(Integer idCategoria, MultipartFile archivo) {
        /*
         TODO: calculate a hash of the multipart and save it into the database.
          The next time a user submits an image, compare the hash of the new image
          with the saved hash and, if it is the same, return immediately to avoid
          calling the S3 API to process the same file multiple times
        */

        // Validaciones
        archivoUtil.esArchivoNoVacio(archivo);
        archivoUtil.esImagen(archivo);

        // Obtener categoria a actualizar
        Categoria categoria = encontrarCategoriaPorIdOLanzarExcepcion(idCategoria);

        // Guardar imagen en S3 y actualizar ruta en la categoria
        String nombreArchivoNuevo = archivoRepository.subir(archivo, CARPETA, categoria.getUrlImagen());

        if (modificacionUtil.textoHaSidoModificado(nombreArchivoNuevo, categoria.getUrlImagen())) {
            categoria.setUrlImagen(nombreArchivoNuevo); // Actualizar la URL de la imagen
            categoriaRepository.save(categoria);
        }
    }

    public void borrarCategoria(Integer idCategoria) {
        // Obtener categoría a borrar. De no existir, no realizar ninguna accion (DELETE is idempotent)
        Optional<Categoria> categoria = categoriaRepository.findById(idCategoria);
        if (categoria.isEmpty()) return;

        // Guardar la URL de la imagen actual
        String urlImagen = categoria.get().getUrlImagen();

        // Borrar la categoria
        categoriaRepository.delete(categoria.get());

        // Si la categoria tenia una imagen asociada, borrarla del repositorio
        if (urlImagen != null && !urlImagen.isBlank())
            archivoRepository.borrar(CARPETA, urlImagen);
    }

    public void borrarImagenCategoria(Integer idCategoria) {
        // Obtener categoría a borrar. De no existir, no realizar ninguna accion (DELETE is idempotent)
        Optional<Categoria> categoria = categoriaRepository.findById(idCategoria);
        if (categoria.isEmpty()) return;

        // Si no hay una imagen asociada a la categoría, no realizar ninguna acción
        if (categoria.get().getUrlImagen() == null || categoria.get().getUrlImagen().isBlank()) return;

        // Borrar la imagen del repositorio
        archivoRepository.borrar(CARPETA, categoria.get().getUrlImagen());

        // Actualizar la URL de la imagen
        categoria.get().setUrlImagen(null);

        categoriaRepository.save(categoria.get());
    }

    private Categoria encontrarCategoriaPorIdOLanzarExcepcion(Integer idCategoria) {
        return categoriaRepository.findById(idCategoria).orElseThrow(
                () -> new EntityNotFoundException(String.format("La categoria con id %s no existe", idCategoria))
        );
    }

    private void lanzarExcepcionSiNombreCategoriaYaExiste(CategoriaRequest categoriaRequest) {
        Optional<Categoria> categoriaHomonima = categoriaRepository.findByNombre(categoriaRequest.getNombre());
        if (categoriaHomonima.isPresent())
            throw new EntityExistsException(String.format("La categoria '%s' ya existe", categoriaRequest.getNombre()));
    }
}
