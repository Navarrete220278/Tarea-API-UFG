package sv.ufg.ordenaenlinea.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv.ufg.ordenaenlinea.model.Categoria;

import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    public Optional<Categoria> findByNombre(String nombre);
}
