package sv.ufg.ordenaenlinea.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import sv.ufg.ordenaenlinea.model.Rol;
import sv.ufg.ordenaenlinea.service.UsuarioDetService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UsuarioDetService usuarioDetService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()

                // No se necesita autenticarse para consultar las categorias y productos
                .antMatchers(HttpMethod.GET,
                        "/api/v1/categorias",
                        "/api/v1/categorias/**",
                        "/api/v1/productos",
                        "/api/v1/productos/**")
                    .permitAll()

                // Para iniciar sesión, refrescar un  token o crear un nuevo usuario, no se necesita autenticación
                .antMatchers(HttpMethod.POST,
                        "/api/v1/auth/login",
                        "/api/v1/auth/refresh",
                        "/usuarios")
                    .permitAll()

                // Para cualquier operación otra de la API se debe estar autenticado
                .antMatchers("/api/**").authenticated()

                // Dar paso libre a cualquier petición que no sea de la API
                .anyRequest().permitAll()

                // Habilitar autentición por formulario (por el momento)
                .and()
                .formLogin();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(usuarioDetService);
        return provider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }
}
