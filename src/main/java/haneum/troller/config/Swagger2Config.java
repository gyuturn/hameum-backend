package haneum.troller.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Swagger2Config {

    @Bean
    public GroupedOpenApi SignApi() {
        return GroupedOpenApi.builder()
                .group("sign-definition")
                .pathsToMatch("/sign/**")
                .build();
    }

    @Bean
    public GroupedOpenApi SearchApi() {
        return GroupedOpenApi.builder()
                .group("search")
                .pathsToMatch("/search/**")
                .build();
    }

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("troller-Rest API")
                        .description("troller API명세")
                        .version("v0.0.1"));
    }
}
