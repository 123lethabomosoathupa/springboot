package tacos.web.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import tacos.Ingredient;
import tacos.Taco;

@Configuration
public class SpringDataRestConfiguration {

  @Bean
  public RepresentationModelProcessor<PagedModel<EntityModel<Taco>>>
  tacoProcessor(EntityLinks links) {

    return new RepresentationModelProcessor<PagedModel<EntityModel<Taco>>>() {
      @Override
      public PagedModel<EntityModel<Taco>> process(
              PagedModel<EntityModel<Taco>> resource) {
        // TacoRepository.findFirst12ByOrderByCreatedAtDesc() is exposed by
        // Spring Data REST as a search resource under /tacos/search/recent
        // (per its @RestResource(path="recent")), not /tacos/recent --
        // that path collides with the /tacos/{id} item-lookup route.
        resource.add(
                links.linkFor(Taco.class)
                        .slash("search")
                        .slash("recent")
                        .withRel("recents"));
        return resource;
      }
    };
  }

  // Spring Data REST hides the @Id field from an entity's own JSON body by
  // default (the identifier is only exposed via the HAL _links.self.href).
  // TacoCloudClient's getIngredientById()/deleteIngredient() round-trip a
  // fetched Ingredient and rely on ingredient.getId() being populated, so
  // it needs to be exposed explicitly here.
  @Bean
  public RepositoryRestConfigurer repositoryRestConfigurer() {
    return new RepositoryRestConfigurer() {
      @Override
      public void configureRepositoryRestConfiguration(
              RepositoryRestConfiguration config, CorsRegistry cors) {
        config.exposeIdsFor(Ingredient.class);
      }
    };
  }

}