package com.bruma;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class ProjectConfig {

    @Autowired
    private UserDetailsService userDetailsService;
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder build, @Lazy BCryptPasswordEncoder passwordEncoder) throws Exception {
        build.userDetailsService(userDetailsService)
             .passwordEncoder(passwordEncoder);
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                .requestMatchers("/", "/index", "/errores/**", "/carrito/**", "/producto/listado", "/producto/listado/**",
                        "/js/**", "/webjars/**", "/css/**", "/images/**", "/login", "/sobre_nosotros", "/contactenos")
                .permitAll()
                // Protección de rutas de producto
                .requestMatchers("/producto/nuevo", "/producto/guardar", "/producto/modificar/**", "/producto/eliminar/**")
                .hasAnyRole("ADMIN", "VENDEDOR")
                // Protección de rutas de perfil de usuario
                .requestMatchers("/perfil/**", "/metodos_pago/**", "/direcciones/**")
                .hasAnyRole("USER", "ADMIN", "VENDEDOR")
                // Protección de todas las rutas de usuario - solo accesibles para ADMIN 
                .requestMatchers("/usuario/**")
                .hasRole("ADMIN")
                .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                .loginPage("/login")
                .permitAll()
                .defaultSuccessUrl("/", true)
                )
                .logout((logout) -> logout.permitAll());
        return http.build();
    }
}