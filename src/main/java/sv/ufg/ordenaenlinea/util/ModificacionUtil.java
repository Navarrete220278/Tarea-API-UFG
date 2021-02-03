package sv.ufg.ordenaenlinea.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ModificacionUtil {
    private final PasswordEncoder passwordEncoder;

    public boolean textoHaSidoModificado(String nuevoValor, String valorAnterior) {
        return !textoEsNuloOEnBlanco(nuevoValor)
                && !Objects.equals(nuevoValor, valorAnterior);
    }

    public boolean passwordHaSidoModificado(String nuevoValor, String valorAnterior) {
        return !passwordEncoder.matches(nuevoValor, valorAnterior);
    }

    public boolean textoEsNuloOEnBlanco(String valor) {
        return valor == null || valor.isBlank();
    }
}
