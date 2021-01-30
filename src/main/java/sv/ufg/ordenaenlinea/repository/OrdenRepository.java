package sv.ufg.ordenaenlinea.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv.ufg.ordenaenlinea.model.Orden;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {
}
