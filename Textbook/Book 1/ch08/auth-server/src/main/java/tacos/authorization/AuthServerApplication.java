package tacos.authorization;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import tacos.authorization.users.User;
import tacos.authorization.users.UserRepository;

@SpringBootApplication
public class AuthServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(AuthServerApplication.class, args);
  }

  @Bean
  public ApplicationRunner dataLoader(
          UserRepository repo, PasswordEncoder encoder,
          RegisteredClientRepository clientRepo) {
    return args -> {
      repo.save(
              new User("habuma", encoder.encode("password"), "ROLE_ADMIN"));
      repo.save(
              new User("tacochef", encoder.encode("password"), "ROLE_ADMIN"));

      // TEMPORARY DIAGNOSTIC — remove once invalid_client is resolved.
      // Prints exactly what's registered so we can compare against
      // what curl is sending, instead of guessing from invalid_client.
      RegisteredClient client = clientRepo.findByClientId("taco-admin-client");
      System.out.println("=== REGISTERED CLIENT DEBUG ===");
      System.out.println("clientId: [" + client.getClientId() + "]");
      System.out.println("clientSecret: [" + client.getClientSecret() + "]");
      System.out.println("authMethods: " + client.getClientAuthenticationMethods());
      System.out.println("grantTypes: " + client.getAuthorizationGrantTypes());
      System.out.println("redirectUris: " + client.getRedirectUris());
      System.out.println("scopes: " + client.getScopes());
      System.out.println("================================");
    };
  }

}