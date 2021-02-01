package sv.ufg.ordenaenlinea.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sv.ufg.ordenaenlinea.model.Producto;
import sv.ufg.ordenaenlinea.repository.ProductoRepository;

@Service
@RequiredArgsConstructor
public class ProductoService {
    private final ProductoRepository productoRepository;

    public Page<Producto> getProductosByCategoria(Integer idCategoria, Pageable pageable) {
        return productoRepository.findByCategoria_Id(idCategoria, pageable);
    }
}
