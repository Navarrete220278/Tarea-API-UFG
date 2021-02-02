package sv.ufg.ordenaenlinea.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv.ufg.ordenaenlinea.model.Orden;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {
    Page<Orden> findByUsuario_Id(Integer idUsuario, Pageable pageable);
    Optional<Orden> findByUsuario_IdAndId(Integer idUsuario, Long idOrden);
}
