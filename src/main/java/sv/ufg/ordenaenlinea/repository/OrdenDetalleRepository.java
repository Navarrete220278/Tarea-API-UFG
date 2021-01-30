package sv.ufg.ordenaenlinea.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv.ufg.ordenaenlinea.model.OrdenDetalle;

@Repository
public interface OrdenDetalleRepository extends JpaRepository<OrdenDetalle, Long> {
}
