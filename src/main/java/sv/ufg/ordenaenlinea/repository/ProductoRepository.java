package sv.ufg.ordenaenlinea.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv.ufg.ordenaenlinea.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
}
