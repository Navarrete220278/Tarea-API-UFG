package sv.ufg.ordenaenlinea.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv.ufg.ordenaenlinea.model.Producto;

import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    Optional<Producto> findByNombre(String nombre);

    Page<Producto> findByCategoria_Id(Integer idCategoria, Pageable pageable);

    int countAllByCategoria_Id(Integer idCategoria);
}
