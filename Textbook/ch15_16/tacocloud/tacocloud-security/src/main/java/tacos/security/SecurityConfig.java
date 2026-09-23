```java
package tacos.security;

// Spring dependency injection
import org.springframework.beans.factory.annotation.Autowired;

// Used to define this class as a Spring configuration class
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Used to specify HTTP methods such as GET, POST, PATCH, etc.
import org.springframework.http.HttpMethod;

// Spring Security authentication configuration
import org.springframework.security.config.annotation
             .authentication.builders.AuthenticationManagerBuilder;

// Used to configure HTTP security rules
import org.springframework.security.config.annotation.web
             .builders.HttpSecurity;

// Enables Spring Security's web security support
import org.springframework.security.config.annotation.web
                        .configuration.EnableWebSecurity;

// Base class used to configure Spring Security
import org.springframework.security.config.annotation.web
                        .configuration.WebSecurityConfigurerAdapter;

// Interface used to retrieve user information during authentication
import org.springframework.security.core.userdetails.UserDetailsService;

// Password encoder used for encoding and verifying passwords
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


// Tells Spring that this class contains configuration information
@Configuration

// Enables Spring Security's web security functionality
@EnableWebSecurity

// WebSecurityConfigurerAdapter allows us to customize Spring Security's configuration
@SuppressWarnings("deprecation")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  /*
   * UserDetailsService is responsible for loading user information
   * when a user attempts to log in.
   *
   * Spring will automatically inject the UserDetailsService bean
   * into this variable.
   */
  @Autowired
  private UserDetailsService userDetailsService;


  /*
   * Configure HTTP security rules.
   *
   * This method determines:
   * - Which endpoints are publicly accessible
   * - Which endpoints require authentication
   * - How login works
   * - How HTTP Basic authentication works
   * - How logout works
   * - Which endpoints are ignored by CSRF protection
   */
  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http

      // Configure authorization rules for incoming HTTP requests
      .authorizeRequests()

        /*
         * Allow HTTP OPTIONS requests without authentication.
         *
         * OPTIONS requests are commonly used by browsers for CORS
         * preflight requests, especially when communicating with
         * an Angular frontend.
         */
        .antMatchers(HttpMethod.OPTIONS).permitAll()


        /*
         * Allow POST requests to create ingredients without
         * requiring authentication.
         */
        .antMatchers(HttpMethod.POST, "/api/ingredients").permitAll()


        /*
         * Allow access to taco and order API endpoints.
         *
         * The ** means that this also includes sub-paths.
         *
         * Example:
         * /api/tacos
         * /api/tacos/123
         * /api/orders
         * /api/orders/123
         */
        .antMatchers("/api/tacos/**", "/api/orders/**")
            .permitAll()

            /*
             * This could be used instead if these endpoints
             * should only be available to authenticated users
             * with the USER role.
             */
            //.access("hasRole('ROLE_USER')")


        /*
         * Allow PATCH requests to /api/ingredients without
         * requiring authentication.
         */
        .antMatchers(HttpMethod.PATCH, "/api/ingredients").permitAll()


        /*
         * Allow all other requests.
         *
         * This effectively makes the rest of the application
         * publicly accessible.
         *
         * NOTE:
         * If you want Spring Security to protect the remaining
         * endpoints, this would normally be replaced with something
         * such as:
         *
         * .antMatchers("/**").authenticated()
         */
        .antMatchers("/**").access("permitAll")


      // Move from authorization configuration to login configuration
      .and()

        /*
         * Enable form-based login.
         *
         * Users can authenticate using a username and password
         * through a web login form.
         */
        .formLogin()

          // Specify the URL where the login page is located
          .loginPage("/login")


      // Add HTTP Basic authentication
      .and()

        /*
         * HTTP Basic authentication allows clients such as REST
         * clients or Postman to authenticate using a username
         * and password in the HTTP Authorization header.
         */
        .httpBasic()

          // Name displayed when authentication is requested
          .realmName("Taco Cloud")


      // Configure logout functionality
      .and()

        .logout()

          /*
           * After successfully logging out, redirect the user
           * back to the application's home page.
           */
          .logoutSuccessUrl("/")


      // Configure Cross-Site Request Forgery protection
      .and()

        .csrf()

          /*
           * CSRF protection is disabled for these endpoints.
           *
           * /h2-console/**:
           * H2 Console requires special handling.
           *
           * /api/**:
           * REST APIs commonly use other authentication mechanisms
           * and may not use browser-based CSRF protection.
           *
           * /actuator/**:
           * Spring Boot Actuator endpoints are also excluded here.
           */
          .ignoringAntMatchers(
              "/h2-console/**",
              "/api/**",
              "/actuator/**"
          )


      /*
       * Allow the H2 database console to be displayed inside
       * an iframe from the same application.
       *
       * Without this, the browser may block the H2 Console because
       * of the X-Frame-Options security header.
       */
      .and()
        .headers()
          .frameOptions()
            .sameOrigin();

  }


  /*
   * Create a PasswordEncoder bean.
   *
   * Spring uses this bean when storing and validating passwords.
   */
  @Bean
  public PasswordEncoder encoder() {

    /*
     * StandardPasswordEncoder was previously being used.
     *
     * It has been commented out here.
     */
    // return new StandardPasswordEncoder("53cr3t");


    /*
     * NoOpPasswordEncoder does not actually encrypt or hash
     * passwords.
     *
     * This is convenient for development/testing because passwords
     * are stored and compared as plain text.
     *
     * IMPORTANT:
     * Do NOT use this in a production application.
     *
     * For production, BCryptPasswordEncoder or another secure
     * password hashing implementation should be used.
     */
    return NoOpPasswordEncoder.getInstance();
  }


  /*
   * Configure how Spring Security should authenticate users.
   *
   * AuthenticationManagerBuilder is used to tell Spring Security:
   *
   * 1. Where user information comes from
   * 2. How passwords should be verified
   */
  @Override
  protected void configure(AuthenticationManagerBuilder auth)
      throws Exception {

    auth

      /*
       * Tell Spring Security to use our UserDetailsService
       * to load users.
       */
      .userDetailsService(userDetailsService)

      /*
       * Tell Spring Security to use the PasswordEncoder defined
       * above when checking user passwords.
       */
      .passwordEncoder(encoder());

  }

}
```
