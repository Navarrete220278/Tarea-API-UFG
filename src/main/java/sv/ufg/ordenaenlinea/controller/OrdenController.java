package sv.ufg.ordenaenlinea.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sv.ufg.ordenaenlinea.model.Estado;
import sv.ufg.ordenaenlinea.model.Orden;
import sv.ufg.ordenaenlinea.request.OrdenRequest;
import sv.ufg.ordenaenlinea.service.OrdenService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OrdenController {
    private final OrdenService ordenService;

    @GetMapping("/ordenes")
    @PreAuthorize("hasRole('EMPLEADO')")
    public Page<Orden> obtenerOrdenes(Pageable pageable) {
        return ordenService.obtenerOrdenes(pageable);
    }

    @GetMapping("/ordenes/{idOrden}")
    @PreAuthorize("hasRole('EMPLEADO')")
    public Orden obtenerOrdenPorId(@PathVariable("idOrden") Long idOrden) {
        return ordenService.obtenerOrdenPorId(idOrden);
    }

    @PostMapping("/usuarios/{idUsuario}/ordenes")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('EMPLEADO') or authentication.name == #idUsuario.toString()")
    public Orden crearOrdenDeUsuario(@PathVariable("idUsuario") Integer idUsuario,
                                     @Valid @RequestBody OrdenRequest ordenRequest) {
        return ordenService.crearOrdenDeUsuario(idUsuario, ordenRequest);
    }

    @GetMapping("/usuarios/{idUsuario}/ordenes")
    @PreAuthorize("hasRole('EMPLEADO') or (authenticated and authentication.name == #idUsuario.toString())")
    public Page<Orden> obtenerOrdenesPorUsuarioId(@PathVariable("idUsuario") Integer idUsuario, Pageable pageable) {
        return ordenService.obtenerOrdenesPorUsuarioId(idUsuario, pageable);
    }

    @GetMapping("/usuarios/{idUsuario}/ordenes/{idOrden}")
    @PreAuthorize("hasRole('EMPLEADO') or (authenticated and authentication.name == #idUsuario.toString())")
    public Orden obtenerOrdenDeUsuario(@PathVariable("idUsuario") Integer idUsuario,
                                       @PathVariable("idOrden") Long idOrden) {
        return ordenService.obtenerOrdenDeUsuario(idUsuario, idOrden);
    }

    @DeleteMapping("/usuarios/{idUsuario}/ordenes/{idOrden}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('EMPLEADO') or (authentication.name == #idUsuario.toString())")
    public void cancelarOrdenDeUsuario(@PathVariable("idUsuario") Integer idUsuario,
                                       @PathVariable("idOrden") Long idOrden) {
        ordenService.cancelarOrdenDeUsuario(idUsuario, idOrden);
    }

    @PutMapping("/ordenes/{idOrden}/preparacion")
    @PreAuthorize("hasRole('EMPLEADO')")
    public void indicarOrdenPreparacion(@PathVariable("idOrden") Long idOrden) {
        ordenService.cambiarEstadoOrden(idOrden, Estado.PREPARACION);
    }

    @PutMapping("/ordenes/{idOrden}/en-transito")
    @PreAuthorize("hasRole('EMPLEADO')")
    public void indicarOrdenEnTransito(@PathVariable("idOrden") Long idOrden) {
        ordenService.cambiarEstadoOrden(idOrden, Estado.EN_TRANSITO);
    }

    @PutMapping("/ordenes/{idOrden}/entregada")
    @PreAuthorize("hasRole('EMPLEADO')")
    public void indicarOrdenEntregada(@PathVariable("idOrden") Long idOrden) {
        ordenService.cambiarEstadoOrden(idOrden, Estado.ENTREGADA);
    }
}
