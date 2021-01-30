package sv.ufg.ordenaenlinea.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv.ufg.ordenaenlinea.model.Producto;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    public Optional<Producto> findByNombre(String nombre);

    public List<Producto> findByCategoriaId(Integer categoryId);
}
