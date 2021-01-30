package sv.ufg.ordenaenlinea.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sv.ufg.ordenaenlinea.model.Categoria;
import sv.ufg.ordenaenlinea.service.CategoriaService;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CategoriaController {
    private final CategoriaService categoriaService;

    @GetMapping("/categorias")
    public Page<Categoria> obtenerCategorias(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                              @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        return categoriaService.obtenerCategorias(page, size);
    }

    @GetMapping(path = "/categorias/{idCategoria}/imagen")
    public byte[] obtenerImagenCategoria(@PathVariable("idCategoria") Integer idCategoria) throws IOException {
        return categoriaService.obtenerImagenCategoria(idCategoria);
    }

    @PostMapping("/categorias")
    public void crearCategoria(@Valid @RequestBody Categoria categoria) {
        categoriaService.crearCategoria(categoria);
    }

    @PutMapping("/categorias/{idCategoria}")
    public void actualizarCategoria(
            @PathVariable("idCategoria") Integer idCategoria,
            @Valid @RequestBody Categoria categoria) {
        categoriaService.actualizarCategoria(idCategoria, categoria);
    }

    @PutMapping(
            path = "/categorias/{idCategoria}/imagen",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public void actualizarImagenCategoria(@PathVariable("idCategoria") Integer idCategoria,
                                     @RequestParam("archivo") MultipartFile archivo) throws IOException {
        categoriaService.actualizarImagenCategoria(idCategoria, archivo);
    }

    @DeleteMapping("/categorias/{idCategoria}")
    public void borrarCategoria(@PathVariable("idCategoria") Integer idCategoria) throws IOException {
        categoriaService.borrarCategoria(idCategoria);
    }

    @DeleteMapping(path = "/categorias/{idCategoria}/imagen")
    public void borrarImagenCategoria(@PathVariable("idCategoria") Integer idCategoria) throws IOException {
        categoriaService.borrarImagenCategoria(idCategoria);
    }
}
