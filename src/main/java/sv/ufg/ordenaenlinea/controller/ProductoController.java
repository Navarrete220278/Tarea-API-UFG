package sv.ufg.ordenaenlinea.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sv.ufg.ordenaenlinea.model.Producto;
import sv.ufg.ordenaenlinea.service.ProductoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductoController {
    private ProductoService productoService;

    @GetMapping("/categorias/{idCategoria}/productos")
    public List<Producto> obtenerProductosCategoria(Integer idCategoria) {
        return productoService.obtenerProductosCategoria(idCategoria);
    }
}
