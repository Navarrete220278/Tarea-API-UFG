package sv.ufg.ordenaenlinea.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

@Getter @RequiredArgsConstructor
public class JwtUserDetails implements UserDetails {
    private final String username;
    private final String password;
    private final long version;
    private final boolean accountNonExpired = true;
    private final boolean accountNonLocked = true;
    private final boolean credentialsNonExpired = true;
    private final boolean enabled;
    private final Set<GrantedAuthority> authorities;
}
