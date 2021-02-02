package sv.ufg.ordenaenlinea.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sv.ufg.ordenaenlinea.model.Rol;
import sv.ufg.ordenaenlinea.model.Usuario;
import sv.ufg.ordenaenlinea.repository.UsuarioRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UsuarioDetService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;
    private final Logger logger = LoggerFactory.getLogger(UsuarioDetService.class);

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Obtener detalles de usuario
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException(String.format("El usuario %s no existe", email))
        );

        // Obtener authorities
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(Rol.CLIENTE.getRoleName()));
        if (usuario.getEsEmpleado() == true)
            authorities.add(new SimpleGrantedAuthority(Rol.EMPLEADO.getRoleName()));

        User usuarioDet = new User(
                usuario.getId().toString(),
                usuario.getPassword(),
                !usuario.getInactivo(),
                true,
                true,
                true,
                authorities
        );

        return usuarioDet;
    }
}
