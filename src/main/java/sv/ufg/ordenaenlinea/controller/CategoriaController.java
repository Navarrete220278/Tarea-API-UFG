package sv.ufg.ordenaenlinea.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sv.ufg.ordenaenlinea.model.Categoria;
import sv.ufg.ordenaenlinea.service.CategoriaService;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categoria")
public class CategoriaController {
    private final CategoriaService categoriaService;

    @Autowired
    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public List<Categoria> obtenerCategorias() {
        return categoriaService.obtenerCategorias();
    }

    @PostMapping
    public void agregarCategoria(@Valid @RequestBody Categoria categoria) {
        categoriaService.agregarCategoria(categoria);
    }

    @PutMapping("/{idCategoria}")
    public void modificarCategoria(
            @PathVariable("idCategoria") Integer idCategoria,
            @Valid @RequestBody Categoria categoria) {
        categoriaService.modificarCategoria(idCategoria, categoria);
    }

    @DeleteMapping("/{idCategoria}")
    public void borrarCategoria(@PathVariable("idCategoria") Integer idCategoria) throws IOException {
        categoriaService.borrarCategoria(idCategoria);
    }

    @PostMapping(
            path = "/{idCategoria}/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public void subirImagenCategoria(@PathVariable("idCategoria") Integer idCategoria,
                                     @RequestParam("archivo") MultipartFile archivo) throws IOException {
        categoriaService.subirImagenCategoria(idCategoria, archivo);
    }

    @GetMapping(path = "/{idCategoria}/image")
    public byte[] descargarImagenCategoria(@PathVariable("idCategoria") Integer idCategoria) throws IOException {
        return categoriaService.descargarImagenCategoria(idCategoria);
    }

    @DeleteMapping(path = "/{idCategoria}/image")
    public void borrarImagenCategoria(@PathVariable("idCategoria") Integer idCategoria) throws IOException {
        categoriaService.borrarImagenCategoria(idCategoria);
    }
}
