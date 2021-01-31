package sv.ufg.ordenaenlinea.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import sv.ufg.ordenaenlinea.model.Categoria;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CategoriaRepositoryTest {
    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByNombre() {
        Categoria pizza = new Categoria();
        pizza.setNombre("Pizza");
        entityManager.persistAndFlush(pizza);

        Optional<Categoria> categoria = categoriaRepository.findByNombre(pizza.getNombre());

        assertThat(categoria)
                .isPresent()
                .map(c -> c.getNombre())
                .hasValue(pizza.getNombre());
    }
}