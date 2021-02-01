package sv.ufg.ordenaenlinea.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sv.ufg.ordenaenlinea.model.Producto;
import sv.ufg.ordenaenlinea.request.ProductoRequest;
import sv.ufg.ordenaenlinea.service.ProductoService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductoController {
    private final ProductoService productoService;

    @GetMapping("/categorias/{idCategoria}/productos")
    public Page<Producto> obtenerProductosPorCategoria(@PathVariable("idCategoria") Integer idCategoria,
                                                       Pageable pageable) {
        return productoService.obtenerProductosPorCategoria(idCategoria, pageable);
    }

    @PostMapping("/categorias/{idCategoria}/productos")
    @ResponseStatus(HttpStatus.CREATED)
    public Producto crearProductoEnCategoria(@PathVariable("idCategoria") Integer idCategoria,
                                             @Valid @RequestBody ProductoRequest productoRequest) {
        return productoService.crearProductoEnCategoria(idCategoria, productoRequest);
    }

    @GetMapping("/productos")
    public Page<Producto> obtenerProductos(Pageable pageable) {
        return productoService.obtenerProductos(pageable);
    }

    @GetMapping("/productos/{idProducto}")
    public Producto obtenerProductoPorId(@PathVariable("idProducto") Integer idProducto) {
        return productoService.obtenerProductoPorId(idProducto);
    }

    @GetMapping("/productos/{idProducto}/imagen")
    public byte[] obtenerImagenProducto(@PathVariable("idProducto") Integer idProducto) {
        return productoService.obtenerImagenProducto(idProducto);
    }

    @PutMapping("/productos/{idProducto}")
    public Producto modificarProductoPorId(@PathVariable("idProducto") Integer idProducto,
                                           @RequestBody ProductoRequest productoRequest) {
        return productoService.modificarProductoPorId(idProducto, productoRequest);
    }

    @PutMapping(
            value = "/productos/{idProducto}/imagen",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public void modificarImagenProducto(@PathVariable("idProducto") Integer idProducto,
                                            @RequestParam("archivo") MultipartFile archivo) {
        productoService.modificarImagenProducto(idProducto, archivo);
    }

    @PutMapping(value = "/productos/{idProducto}/categorias/{idCategoria}")
    public Producto modificarCategoriaDeProducto(@PathVariable("idProducto") Integer idProducto,
                                                 @PathVariable("idCategoria") Integer idCategoria) {
        return productoService.modificarCategoriaDeProducto(idProducto, idCategoria);
    }

    @DeleteMapping("/productos/{idProducto}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void borrarProducto(@PathVariable("idProducto") Integer idProducto) {
        productoService.borrarProducto(idProducto);
    }

    @DeleteMapping("/productos/{idProducto}/imagen")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void borrarImagenProducto(@PathVariable("idProducto") Integer idProducto) {
        productoService.borrarImagenProducto(idProducto);
    }
}
