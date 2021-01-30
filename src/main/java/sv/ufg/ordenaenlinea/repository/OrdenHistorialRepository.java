package sv.ufg.ordenaenlinea.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv.ufg.ordenaenlinea.model.OrdenHistorial;

@Repository
public interface OrdenHistorialRepository extends JpaRepository<OrdenHistorial, Long> {
}
