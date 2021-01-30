package sv.ufg.ordenaenlinea.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sv.ufg.ordenaenlinea.model.Producto;
import sv.ufg.ordenaenlinea.repository.ProductoRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {
    private final ProductoRepository productoRepository;

    public List<Producto> obtenerProductosCategoria(Integer idCategoria) {
        return productoRepository.findByCategoriaId(idCategoria);
    }
}
