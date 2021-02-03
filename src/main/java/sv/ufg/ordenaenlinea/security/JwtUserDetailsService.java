package sv.ufg.ordenaenlinea.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sv.ufg.ordenaenlinea.model.Rol;
import sv.ufg.ordenaenlinea.model.Usuario;
import sv.ufg.ordenaenlinea.repository.UsuarioRepository;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Obtener detalles de usuario
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException(String.format("El usuario %s no existe", email))
        );

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(Rol.CLIENTE.getRoleName()));
        if (usuario.getEsEmpleado())
            authorities.add(new SimpleGrantedAuthority(Rol.EMPLEADO.getRoleName()));

        JwtUserDetails jwtUserDetails = new JwtUserDetails(
                usuario.getId().toString(),
                usuario.getPassword(),
                usuario.getVersionToken(),
                !usuario.getInactivo(),
                authorities
        );

        return jwtUserDetails;
    }
}
