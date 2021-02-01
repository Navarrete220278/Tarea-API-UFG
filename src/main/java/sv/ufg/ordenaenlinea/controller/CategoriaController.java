package sv.ufg.ordenaenlinea.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sv.ufg.ordenaenlinea.model.Categoria;
import sv.ufg.ordenaenlinea.service.CategoriaService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class CategoriaController {
    private final CategoriaService categoriaService;

    @GetMapping("/api/v1/categorias")
    public Page<Categoria> obtenerCategorias(Pageable pageable) {
        return categoriaService.obtenerCategorias(pageable);
    }

    @GetMapping("/api/v1/categorias/{id}")
    public Categoria obtenerCategoriasPorId(@PathVariable("id") Integer idCategoria) {
        return categoriaService.obtenerCategoriaPorId(idCategoria);
    }

    @GetMapping("/api/v1/categorias/{id}/imagen")
    public byte[] obtenerImagenCategoria(@PathVariable("id") Integer idCategoria) {
        return categoriaService.obtenerImagenCategoria(idCategoria);
    }

    @PostMapping("/api/v1/categorias")
    @ResponseStatus(HttpStatus.CREATED)
    public Categoria crearCategoria(@Valid @RequestBody Categoria categoria) {
        return categoriaService.postearCategoria(categoria);
    }

    @PutMapping("/api/v1/categorias/{id}")
    public Categoria modificarCategoria(@PathVariable("id") Integer idCategoria,
                                        @Valid @RequestBody Categoria categoria) {
        return categoriaService.modificarCategoria(idCategoria, categoria);
    }

    @PutMapping(
            value = "/api/v1/categorias/{id}/imagen",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public void modificarImagenCategoria(@PathVariable("id") Integer idCategoria,
                                         @RequestParam("archivo") MultipartFile archivo) {
        categoriaService.modificarImagenCategoria(idCategoria, archivo);
    }

    @DeleteMapping("/api/v1/categorias/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void borrarCategoria(@PathVariable("id") Integer idCategoria) {
        categoriaService.borrarCategoria(idCategoria);
    }

    @DeleteMapping("/api/v1/categorias/{id}/imagen")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void borrarImagenCategoria(@PathVariable("id") Integer idCategoria) {
        categoriaService.borrarImagenCategoria(idCategoria);
    }
}
