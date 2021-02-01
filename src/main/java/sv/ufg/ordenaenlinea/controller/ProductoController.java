package sv.ufg.ordenaenlinea.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import sv.ufg.ordenaenlinea.model.Producto;
import sv.ufg.ordenaenlinea.service.ProductoService;

@RestController
@RequiredArgsConstructor
public class ProductoController {
    private final ProductoService productoService;

    @GetMapping("/api/v1/categorias/{id}/productos")
    public Page<Producto> getProductosByCategoria(@PathVariable("id") Integer idCategoria, Pageable pageable) {
        return productoService.getProductosByCategoria(idCategoria, pageable);
    }
}
