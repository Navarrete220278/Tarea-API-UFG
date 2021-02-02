package sv.ufg.ordenaenlinea.util;

import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ModificacionUtil {
    public boolean textoHaSidoModificado(String nuevoValor, String valorAnterior) {
        return !textoEsNuloOEnBlanco(nuevoValor)
                && !Objects.equals(nuevoValor, valorAnterior);
    }

    // TODO: modificar este metodo para comparar hashes en lugar de passwords planos
    public boolean passwordHaSidoModificado(String nuevoValor, String valorAnterior) {
        return !textoHaSidoModificado(nuevoValor, valorAnterior);
    }

    public boolean textoEsNuloOEnBlanco(String valor) {
        return valor == null || valor.isBlank();
    }
}
