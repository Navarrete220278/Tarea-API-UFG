package sv.ufg.ordenaenlinea.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sv.ufg.ordenaenlinea.model.Usuario;
import sv.ufg.ordenaenlinea.request.UsuarioRequest;
import sv.ufg.ordenaenlinea.service.UsuarioService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;

    @GetMapping("/usuarios")
    public Page<Usuario> obtenerUsuarios(Pageable pageable) {
        return usuarioService.obtenerUsuarios(pageable);
    }

    @GetMapping("/usuarios/{idUsuario}")
    public Usuario obtenerUsuariosPorId(@PathVariable("idUsuario") Integer idUsuario) {
        return usuarioService.obtenerUsuarioPorId(idUsuario);
    }

    @GetMapping("/usuarios/{idUsuario}/imagen")
    public byte[] obtenerImagenUsuario(@PathVariable("idUsuario") Integer idUsuario) {
        return usuarioService.obtenerImagenUsuario(idUsuario);
    }

    @PostMapping("/usuarios")
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario crearUsuario(@Valid @RequestBody UsuarioRequest usuarioRequest) {
        return usuarioService.crearUsuario(usuarioRequest);
    }

    @PutMapping("/usuarios/{idUsuario}")
    public Usuario modificarUsuario(@PathVariable("idUsuario") Integer idUsuario,
                                        @RequestBody UsuarioRequest usuarioRequest) {
        return usuarioService.modificarUsuario(idUsuario, usuarioRequest);
    }

    @PutMapping(
            value = "/usuarios/{idUsuario}/imagen",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public void modificarImagenUsuario(@PathVariable("idUsuario") Integer idUsuario,
                                         @RequestParam("archivo") MultipartFile archivo) {
        usuarioService.modificarImagenUsuario(idUsuario, archivo);
    }

    @DeleteMapping("/usuarios/{idUsuario}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void borrarUsuario(@PathVariable("idUsuario") Integer idUsuario) {
        usuarioService.borrarUsuario(idUsuario);
    }

    @DeleteMapping("/usuarios/{idUsuario}/imagen")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void borrarImagenUsuario(@PathVariable("idUsuario") Integer idUsuario) {
        usuarioService.borrarImagenUsuario(idUsuario);
    }

    @PutMapping("/usuarios/{idUsuario}/admin")
    public void agregarRolAdmin(@PathVariable("idUsuario") Integer idUsuario) {
        usuarioService.agregarRolAdmin(idUsuario);
    }

    @DeleteMapping("/usuarios/{idUsuario}/admin")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerRolAdmin(@PathVariable("idUsuario") Integer idUsuario) {
        usuarioService.removerRolAdmin(idUsuario);
    }
}
