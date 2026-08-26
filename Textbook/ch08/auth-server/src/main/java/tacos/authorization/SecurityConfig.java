package tacos.authorization;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.
        HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.
        EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import tacos.authorization.users.UserRepository;

@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
            throws Exception {
        return http
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests.anyRequest().authenticated()
                )

                .formLogin()

                .and().build();
    }

    @Bean
    UserDetailsService userDetailsService(UserRepository userRepo) {
        return username -> userRepo.findByUsername(username);
    }

    // Swapped BCryptPasswordEncoder for DelegatingPasswordEncoder.
    // BCryptPasswordEncoder can't interpret the {id} prefix scheme
    // (e.g. {noop}, {bcrypt}), so it was trying to bcrypt-match the
    // literal string "{noop}secret" as if it were a hash -> always
    // failed -> invalid_client on every token exchange, regardless
    // of the actual secret typed in curl. DelegatingPasswordEncoder
    // reads the prefix and dispatches to the right encoder, while
    // still defaulting new encode() calls (e.g. for user passwords)
    // to bcrypt under the hood.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}