package com.bibloteca.beta.config;

import com.bibloteca.beta.services.AuthorService;
import com.bibloteca.beta.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomerService customerService;
    private final AuthorService authorService;
    private final PasswordEncoder passwordEncoder;
    //private final PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfig(AuthorService authorService, CustomerService customerService, PasswordEncoder passwordEncoder) {
        this.customerService = customerService;
        this.authorService = authorService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        DaoAuthenticationProvider customerProvider = new DaoAuthenticationProvider();
        customerProvider.setUserDetailsService(customerService);
        customerProvider.setPasswordEncoder(passwordEncoder);

        DaoAuthenticationProvider authorProvider = new DaoAuthenticationProvider();
        authorProvider.setUserDetailsService(authorService);
        authorProvider.setPasswordEncoder(passwordEncoder);

        auth.authenticationProvider(customerProvider);
        auth.authenticationProvider(authorProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {//configura la seguridad del protocolo

        http.authorizeRequests().antMatchers("/css/**", "/js/**", "/img/**").permitAll()
                .and()
                .formLogin().loginPage("/login")
                .loginProcessingUrl("/logincheck")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/")
                .failureUrl("/login?error=error").permitAll()
                .and()
                .logout().logoutUrl("/logout")
                .logoutSuccessUrl("/login").permitAll()
                .and().csrf().disable();
    }

    /*@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }*/
}
