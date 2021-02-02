package sv.ufg.ordenaenlinea.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sv.ufg.ordenaenlinea.model.Categoria;
import sv.ufg.ordenaenlinea.model.Producto;
import sv.ufg.ordenaenlinea.model.Usuario;
import sv.ufg.ordenaenlinea.repository.ArchivoRepository;
import sv.ufg.ordenaenlinea.repository.CategoriaRepository;
import sv.ufg.ordenaenlinea.repository.ProductoRepository;
import sv.ufg.ordenaenlinea.request.ProductoRequest;
import sv.ufg.ordenaenlinea.util.ArchivoUtil;
import sv.ufg.ordenaenlinea.util.ModificacionUtil;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductoService {
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ArchivoRepository archivoRepository;
    private final ModificacionUtil modificacionUtil;
    private final ArchivoUtil archivoUtil;
    private final String CARPETA = "producto";

    public Page<Producto> obtenerProductosPorCategoria(Integer idCategoria, Pageable pageable) {
        return productoRepository.findByCategoria_Id(idCategoria, pageable);
    }

    public Producto crearProductoEnCategoria(Integer idCategoria, ProductoRequest productoRequest) {
        // Obtener la categoria a la que se agregará el producto
        Categoria categoria = encontrarCategoriaPorIdOLanzarExcepcion(idCategoria);

        // Verificar que el producto no exista
        lanzarExcepcionSiNombreProductoYaExiste(productoRequest);

        // Actualizar la categoria y guardar el producto en la BD
        Producto producto = Producto.of(productoRequest);
        producto.setCategoria(categoria);
        return productoRepository.save(producto);
    }

    public Page<Producto> obtenerProductos(Pageable pageable) {
        return productoRepository.findAll(pageable);
    }

    public Producto obtenerProductoPorId(Integer idProducto) {
        return encontrarProductoPorIdOLanzarExcepcion(idProducto);
    }

    public Producto modificarProducto(Integer idProducto, ProductoRequest productoRequest) {
        Producto producto = encontrarProductoPorIdOLanzarExcepcion(idProducto);

        boolean modificado = false; // Variable de control para saber si el producto fue modificado

        // Detectar cambios en el nombre
        if (modificacionUtil.textoHaSidoModificado(productoRequest.getNombre(), producto.getNombre())) {
            lanzarExcepcionSiNombreProductoYaExiste(productoRequest);
            producto.setNombre(productoRequest.getNombre());
            modificado = true;
        }

        // Detectar cambios en el precio
        if (productoRequest.getPrecio() != null && !Objects.equals(producto.getPrecio(), productoRequest.getPrecio())) {
            producto.setPrecio(productoRequest.getPrecio());
            modificado = true;
        }

        if (!modificado) return producto;
        else return productoRepository.save(producto);
    }

    public Producto modificarCategoriaDeProducto(Integer idProducto, Integer idCategoria) {
        Producto producto = encontrarProductoPorIdOLanzarExcepcion(idProducto);

        // Si la categoria no ha cambiado, no realizar ninguna accion
        if (producto.getCategoria().getId().equals(idCategoria)) return producto;

        // Verificar que la nueva categoria exista
        Categoria categoria = encontrarCategoriaPorIdOLanzarExcepcion(idCategoria);

        // Actualizar la categoria en la BD
        producto.setCategoria(categoria);
        return productoRepository.save(producto);
    }
    
    public void modificarImagenProducto(Integer idProducto, MultipartFile archivo) {
        /*
         TODO: calculate a hash of the multipart and save it into the database.
          The next time a user submits an image, compare the hash of the new image
          with the saved hash and, if it is the same, return immediately to avoid
          calling the S3 API to process the same file multiple times
        */

        // Validaciones
        archivoUtil.esArchivoNoVacio(archivo);
        archivoUtil.esImagen(archivo);

        // Obtener producto a actualizar
        Producto producto = encontrarProductoPorIdOLanzarExcepcion(idProducto);

        // Guardar imagen en S3 y actualizar ruta en el producto
        String nombreArchivoNuevo = archivoRepository.subir(archivo, CARPETA, producto.getUrlImagen());

        if (!modificacionUtil.textoHaSidoModificado(producto.getUrlImagen(), nombreArchivoNuevo)) {
            producto.setUrlImagen(nombreArchivoNuevo); // Actualizar la URL de la imagen
            productoRepository.save(producto);
        }
    }

    public byte[] obtenerImagenProducto(Integer idProducto) {
        Producto producto = encontrarProductoPorIdOLanzarExcepcion(idProducto);

        if (producto.getUrlImagen() == null || producto.getUrlImagen().isBlank())
            return new byte[0];
        else
            return archivoRepository.descargar(CARPETA, producto.getUrlImagen());
    }

    public void borrarProducto(Integer idProducto) {
        // Obtener producto a borrar. De no existir, no realizar ninguna accion (DELETE is idempotent)
        Optional<Producto> producto = productoRepository.findById(idProducto);
        if (producto.isEmpty()) return;

        // Guardar la URL de la imagen actual
        String urlImagen = producto.get().getUrlImagen();

        // Borrar la producto
        productoRepository.delete(producto.get());

        // Si el producto tenia una imagen asociada, borrarla del repositorio
        if (urlImagen != null && !urlImagen.isBlank())
            archivoRepository.borrar(CARPETA, urlImagen);
    }

    public void borrarImagenProducto(Integer idProducto) {
        // Obtener producto a borrar. De no existir, no realizar ninguna accion (DELETE is idempotent)
        Optional<Producto> producto = productoRepository.findById(idProducto);
        if (producto.isEmpty()) return;

        // Si no hay una imagen asociada al producto, no realizar ninguna acción
        if (producto.get().getUrlImagen() == null || producto.get().getUrlImagen().isBlank()) return;

        // Borrar la imagen del repositorio
        archivoRepository.borrar(CARPETA, producto.get().getUrlImagen());

        // Actualizar la URL de la imagen
        producto.get().setUrlImagen(null);

        productoRepository.save(producto.get());
    }

    private Categoria encontrarCategoriaPorIdOLanzarExcepcion(Integer idCategoria) {
        return categoriaRepository.findById(idCategoria).orElseThrow(
                () -> new EntityNotFoundException(String.format("La categoria con id %s no existe", idCategoria))
        );
    }

    private Producto encontrarProductoPorIdOLanzarExcepcion(Integer idProducto) {
        return productoRepository.findById(idProducto).orElseThrow(
                () -> new EntityNotFoundException(String.format("El producto con id %s no existe", idProducto))
        );
    }

    private void lanzarExcepcionSiNombreProductoYaExiste(ProductoRequest productoRequest) {
        Optional<Producto> productoHomonimo = productoRepository.findByNombre(productoRequest.getNombre());
        if (productoHomonimo.isPresent())
            throw new EntityExistsException(String.format("El producto '%s' ya existe", productoRequest.getNombre()));
    }
}
