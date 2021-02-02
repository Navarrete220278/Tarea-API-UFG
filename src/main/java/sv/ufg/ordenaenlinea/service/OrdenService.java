package sv.ufg.ordenaenlinea.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.ufg.ordenaenlinea.model.*;
import sv.ufg.ordenaenlinea.repository.OrdenRepository;
import sv.ufg.ordenaenlinea.request.OrdenRequest;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrdenService {
    private final OrdenRepository ordenRepository;
    private final UsuarioService usuarioService;
    private final ProductoService productoService;

    @Value("${app.entrega.tiempoMinimoMinutos}")
    private Integer tiempoEntrgaMinimoMinutos;

    public Page<Orden> obtenerOrdenes(Pageable pageable) {
        return ordenRepository.findAll(pageable);
    }

    public Orden obtenerOrdenPorId(Long idOrden) {
        return encontrarOrdenPorIdOLanzarExcepcion(idOrden);
    }

    private Orden encontrarOrdenPorIdOLanzarExcepcion(Long idOrden) {
        return ordenRepository.findById(idOrden).orElseThrow(
                () -> new EntityNotFoundException(String.format("La orden %s no existe", idOrden))
        );
    }

    @Transactional
    public Orden crearOrdenDeUsuario(Integer idUsuario, OrdenRequest ordenRequest) {
        // Validar que la orden tenga al menos una linea
        List<OrdenRequest.Linea> lineas = ordenRequest.getLineas();
        if (lineas.size() < 1)
            throw new IllegalArgumentException("No se puede crear una orden sin lineas");

        // Obtener el usuario al que corresponde la orden
        Usuario usuario = usuarioService.obtenerUsuarioPorId(idUsuario);

        // Configurar detalles generales de la orden
        Orden orden = new Orden();
        orden.setUsuario(usuario);
        LocalDateTime ahora = LocalDateTime.now();
        orden.setFechaCreada(ahora);

        // El tiempo mininmo de entrega es 30 mins, aunque el usuario puede especificar
        // una hora especifica, siempre que sea posterior al tiempo mínimo de entrega.
        // Si el tiempo de entrega es muy corto, se reemplaza con el tiempo mínimo
        LocalDateTime fechaMinima = ahora.plusMinutes(tiempoEntrgaMinimoMinutos);
        LocalDateTime fechaSolicitada = ordenRequest.getFechaSolicitada();
        if (fechaSolicitada != null) {
            orden.setFechaSolicitada(fechaSolicitada.isBefore(fechaMinima) ? fechaMinima : fechaSolicitada);
        } else {
            orden.setFechaSolicitada(fechaMinima);
        }

        // Crea un nuevo registro en el historial de la orden
        orden.setEstado(Estado.getEstadoPorDefecto());
        OrdenHistorial ordenHistorial = crearEntradaHistorial(usuario, orden,
                Estado.getEstadoPorDefecto(), "Orden creada");
        orden.getHistorial().add(ordenHistorial);

        // Crea las lineas de detalle de la orden
        for (OrdenRequest.Linea linea: lineas) {
            // Obtener un nuevo detalle de orden
            OrdenDetalle ordenDetalle = crearDetalleOrden(orden, linea);

            // Agregar el detalle a la orden
            orden.getDetalles().add(ordenDetalle);
        }

        // Guarda la orden en la base de datos y la devuelve al usuario
        // Esto almacena el historial y los detalles en cascada debido a la
        // anotación cascade = {CascadeType.MERGE, CascadeType.PERSIST} de
        // las relaciones oneToMany de la clase Orden
        return ordenRepository.save(orden);
    }

    private OrdenDetalle crearDetalleOrden(Orden orden, OrdenRequest.Linea linea) {
        Producto producto = productoService.obtenerProductoPorId(linea.getIdProducto());
        OrdenDetalle ordenDetalle = new OrdenDetalle();
        ordenDetalle.setOrden(orden);
        ordenDetalle.setProducto(producto);
        ordenDetalle.setCantidad(linea.getCantidad());
        ordenDetalle.setPrecio(producto.getPrecio()); // Se guarda el precio actual
        return ordenDetalle;
    }

    public Page<Orden> obtenerOrdenesPorUsuarioId(Integer idUsuario, Pageable pageable) {
        return ordenRepository.findByUsuario_Id(idUsuario, pageable);
    }

    public Orden obtenerOrdenDeUsuario(Integer idUsuario, Long idOrden) {
        return ordenRepository.findByUsuario_IdAndId(idUsuario, idOrden).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("El usuario %s no posee ninguna orden con id %s", idUsuario, idOrden)
                )
        );
    }

    @Transactional
    public void cancelarOrdenDeUsuario(Integer idUsuario, Long idOrden) {
        Orden orden = obtenerOrdenDeUsuario(idUsuario, idOrden);
        procesarCambioEstado(orden.getUsuario(), orden, Estado.CANCELADA);
    }

    @Transactional
    public Orden cambiarEstadoOrden(Long idOrden, Estado nuevoEstado) {
        Orden orden = obtenerOrdenPorId(idOrden);

        // TODO: obtener el id de usuario de la sesión, en lugar de usar el mismo usuario de la orden
        Usuario usuario = orden.getUsuario();
        return procesarCambioEstado(usuario, orden, nuevoEstado);
    }

    protected Orden procesarCambioEstado(Usuario usuario, Orden orden, Estado nuevoEstado) {
        // Obtener estado actual de la orden
        Estado estado = orden.getEstado();

        // Si el estado no se ha modificado, no realizar ninguna acción
        if (Objects.equals(estado, nuevoEstado)) return orden;

        // No se puede modificar órdenes con estados inmutables (CANCELADA, ENTREGADA)
        if (estado.esInmutable())
            throw new IllegalStateException(
                    String.format("La orden %s se encuentra %s y no se puede modificar", orden.getId(), estado.name())
            );

        // Verificar que el nuevo estado tenga un nuevo de secuencia mayor (por ejemplo, EN_TRANSITO
        // tiene una secuencia mayor a PENDIENTE, ya que en el orden lógico, ocurre después).
        if (nuevoEstado.getSecuencia() < estado.getSecuencia())
            throw new IllegalArgumentException(
                    String.format("La orden %s tiene estado %s y no puede marcarse como %s",
                            orden.getId(), estado.name(), nuevoEstado.name())
            );

        // Inserta un nuevo registro en el historial de la orden y la guarda en la BD
        OrdenHistorial ordenHistorial = crearEntradaHistorial(usuario, orden, nuevoEstado,
                String.format("Estado anterior: %s", estado.name()));
        orden.getHistorial().add(ordenHistorial);
        orden.setEstado(nuevoEstado);

        return ordenRepository.save(orden);
    }

    private OrdenHistorial crearEntradaHistorial(Usuario usuario, Orden orden, Estado estado, String comentario) {
        OrdenHistorial ordenHistorial = new OrdenHistorial();
        ordenHistorial.setFecha(LocalDateTime.now());
        ordenHistorial.setOrden(orden);
        ordenHistorial.setEstado(estado);
        ordenHistorial.setUsuario(usuario);
        ordenHistorial.setComentarios(comentario);
        return ordenHistorial;
    }
}