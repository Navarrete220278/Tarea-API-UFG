package sv.ufg.ordenaenlinea.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sv.ufg.ordenaenlinea.model.Usuario;
import sv.ufg.ordenaenlinea.repository.ArchivoRepository;
import sv.ufg.ordenaenlinea.repository.UsuarioRepository;
import sv.ufg.ordenaenlinea.request.UsuarioRequest;
import sv.ufg.ordenaenlinea.util.ArchivoUtil;
import sv.ufg.ordenaenlinea.util.ModificacionUtil;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final ArchivoRepository archivoRepository;
    private final ArchivoUtil archivoUtil;
    private final ModificacionUtil modificacionUtil;
    private final PasswordEncoder passwordEncoder;
    private final String CARPETA = "usuario";

    public Page<Usuario> obtenerUsuarios(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    public Usuario obtenerUsuarioPorId(Integer idUsuario) {
        Usuario usuario = encontrarUsuarioPorIdOLanzarExcepcion(idUsuario);
        return usuario;
    }

    public byte[] obtenerImagenUsuario(Integer idUsuario) {
        Usuario usuario = encontrarUsuarioPorIdOLanzarExcepcion(idUsuario);

        if (usuario.getUrlImagen() == null || usuario.getUrlImagen().isBlank())
            return new byte[0];
        else
            return archivoRepository.descargar(CARPETA, usuario.getUrlImagen());
    }

    @Transactional
    public Usuario crearUsuario(UsuarioRequest usuarioRequest) {
        // Verificar que el email no esté registrado
        lanzarExcepcionSiEmailUsuarioYaExiste(usuarioRequest);

        // Calcular el hash del password
        usuarioRequest.setPassword(passwordEncoder.encode(usuarioRequest.getPassword()));

        // Guardar el password en la base de datos
        return usuarioRepository.save(Usuario.of(usuarioRequest));
    }

    public Usuario modificarUsuario(Integer idUsuario, UsuarioRequest usuarioRequest) {
        Usuario usuario = encontrarUsuarioPorIdOLanzarExcepcion(idUsuario);

        boolean modificado = false; // Variable de control para saber si el usuario fue modificado

        // Detectar cambios en el email
        if (modificacionUtil.textoHaSidoModificado(usuarioRequest.getEmail(), usuario.getEmail())) {
            lanzarExcepcionSiEmailUsuarioYaExiste(usuarioRequest);
            usuario.setEmail(usuarioRequest.getEmail());
            modificado = true;
        }

        // Detectar cambios en el password
        if (modificacionUtil.passwordHaSidoModificado(usuarioRequest.getPassword(), usuario.getPassword())) {
            usuario.setPassword(passwordEncoder.encode(usuarioRequest.getPassword()));
            modificado = true;
        }

        // Detectar cambios en la direccion
        if (modificacionUtil.textoHaSidoModificado(usuarioRequest.getDireccion(), usuario.getDireccion())) {
            usuario.setDireccion(usuarioRequest.getDireccion());
            modificado = true;
        }
        
        // Detectar cambios en el telefono
        if (modificacionUtil.textoHaSidoModificado(usuarioRequest.getTelefono(), usuario.getTelefono())) {
            usuario.setTelefono(usuarioRequest.getTelefono());
            modificado = true;
        }
        
        if (!modificado) return usuario;
        else return usuarioRepository.save(usuario);
    }

    public void modificarImagenUsuario(Integer idUsuario, MultipartFile archivo) {
        /*
         TODO: calculate a hash of the multipart and save it into the database.
          The next time a user submits an image, compare the hash of the new image
          with the saved hash and, if it is the same, return immediately to avoid
          calling the S3 API to process the same file multiple times
        */

        // Validaciones
        archivoUtil.esArchivoNoVacio(archivo);
        archivoUtil.esImagen(archivo);

        // Obtener usuario a actualizar
        Usuario usuario = encontrarUsuarioPorIdOLanzarExcepcion(idUsuario);

        // Guardar imagen en S3 y actualizar ruta en el usuario
        String nombreArchivoNuevo = archivoRepository.subir(archivo, CARPETA, usuario.getUrlImagen());

        if (!modificacionUtil.textoHaSidoModificado(usuario.getUrlImagen(), nombreArchivoNuevo)) {
            usuario.setUrlImagen(nombreArchivoNuevo); // Actualizar la URL de la imagen
            usuarioRepository.save(usuario);
        }
    }

    public void borrarUsuario(Integer idUsuario) {
        // Obtener usuario a borrar. De no existir, no realizar ninguna accion (DELETE is idempotent)
        Optional<Usuario> usuario = usuarioRepository.findById(idUsuario);
        if (usuario.isEmpty()) return;

        // Guardar la URL de la imagen actual
        String urlImagen = usuario.get().getUrlImagen();

        // Marcar la cuenta del usuario como inactiva, si no lo estuviera
        if (!usuario.get().getInactivo()) {
            usuario.get().setInactivo(true);
            usuarioRepository.save(usuario.get());
        }
    }

    public void borrarImagenUsuario(Integer idUsuario) {
        // Obtener categoría a borrar. De no existir, no realizar ninguna accion (DELETE is idempotent)
        Optional<Usuario> usuario = usuarioRepository.findById(idUsuario);
        if (usuario.isEmpty()) return;

        // Si no hay una imagen asociada a la categoría, no realizar ninguna acción
        if (modificacionUtil.textoEsNuloOEnBlanco(usuario.get().getUrlImagen())) return;

        // Borrar la imagen del repositorio
        archivoRepository.borrar(CARPETA, usuario.get().getUrlImagen());

        // Actualizar la URL de la imagen
        usuario.get().setUrlImagen(null);

        usuarioRepository.save(usuario.get());
    }

    public void agregarEmpleado(Integer idUsuario) {
        Usuario usuario = encontrarUsuarioPorIdOLanzarExcepcion(idUsuario);
        if (!usuario.getEsEmpleado()) {
            usuario.setEsEmpleado(true);
            usuarioRepository.save(usuario);
        }
    }

    public void removerEmpleado(Integer idUsuario) {
        Usuario usuario = encontrarUsuarioPorIdOLanzarExcepcion(idUsuario);
        if (usuario.getEsEmpleado()) {
            usuario.setEsEmpleado(false);
            usuarioRepository.save(usuario);
        }
    }

    private Usuario encontrarUsuarioPorIdOLanzarExcepcion(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario).orElseThrow(
                () -> new EntityNotFoundException(String.format("El usuario con id %s no existe", idUsuario))
        );
    }

    private void lanzarExcepcionSiEmailUsuarioYaExiste(UsuarioRequest usuarioRequest) {
        Optional<Usuario> usuarioHomonimo = usuarioRepository.findByEmail(usuarioRequest.getEmail());
        if (usuarioHomonimo.isPresent())
            throw new EntityExistsException(String.format("El usuario '%s' ya existe", usuarioRequest.getEmail()));
    }

    @Transactional
    public void invalidarRefreshTokens(Integer idUsuario) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(idUsuario);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            usuario.setVersionToken(usuario.getVersionToken() + 1);
            usuarioRepository.save(usuario);
        }
    }
}
