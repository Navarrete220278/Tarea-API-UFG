package sv.ufg.ordenaenlinea.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sv.ufg.ordenaenlinea.model.Categoria;
import sv.ufg.ordenaenlinea.request.CategoriaRequest;
import sv.ufg.ordenaenlinea.service.CategoriaService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CategoriaController {
    private final CategoriaService categoriaService;

    @GetMapping("/categorias")
    public Page<Categoria> obtenerCategorias(Pageable pageable) {
        return categoriaService.obtenerCategorias(pageable);
    }

    @GetMapping("/categorias/{idCategoria}")
    public Categoria obtenerCategoriaPorId(@PathVariable("idCategoria") Integer idCategoria) {
        return categoriaService.obtenerCategoriaPorId(idCategoria);
    }

    @GetMapping("/categorias/{idCategoria}/imagen")
    public byte[] obtenerImagenCategoria(@PathVariable("idCategoria") Integer idCategoria) {
        return categoriaService.obtenerImagenCategoria(idCategoria);
    }

    @PostMapping("/categorias")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('EMPLEADO')")
    public Categoria crearCategoria(@Valid @RequestBody CategoriaRequest categoriaRequest) {
        return categoriaService.crearCategoria(categoriaRequest);
    }

    @PutMapping("/categorias/{idCategoria}")
    @PreAuthorize("hasRole('EMPLEADO')")
    public Categoria modificarCategoria(@PathVariable("idCategoria") Integer idCategoria,
                                        @RequestBody CategoriaRequest categoriaRequest) {
        return categoriaService.modificarCategoria(idCategoria, categoriaRequest);
    }

    @PutMapping(
            value = "/categorias/{idCategoria}/imagen",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole('EMPLEADO')")
    public void modificarImagenCategoria(@PathVariable("idCategoria") Integer idCategoria,
                                         @RequestParam("archivo") MultipartFile archivo) {
        categoriaService.modificarImagenCategoria(idCategoria, archivo);
    }

    @DeleteMapping("/categorias/{idCategoria}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('EMPLEADO')")
    public void borrarCategoria(@PathVariable("idCategoria") Integer idCategoria) {
        categoriaService.borrarCategoria(idCategoria);
    }

    @DeleteMapping("/categorias/{idCategoria}/imagen")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('EMPLEADO')")
    public void borrarImagenCategoria(@PathVariable("idCategoria") Integer idCategoria) {
        categoriaService.borrarImagenCategoria(idCategoria);
    }
}
