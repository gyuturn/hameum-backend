package haneum.troller.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Swagger2Config {

    @Bean
    public GroupedOpenApi SignUpApi() {
        return GroupedOpenApi.builder()
                .group("signUp-definition")
                .pathsToMatch("/api/member/sign-up/**")
                .build();
    }
    @Bean
    public GroupedOpenApi SignInApi() {
        return GroupedOpenApi.builder()
                .group("signIn-definition")
                .pathsToMatch("/api/member/sign-in/**")
                .build();
    }
    @Bean
    public GroupedOpenApi JwtApi() {
        return GroupedOpenApi.builder()
                .group("JWT-definition")
                .pathsToMatch("/api/jwt/**")
                .build();
    }

    @Bean
    public GroupedOpenApi userFullSearchApi() {
        return GroupedOpenApi.builder()
                .group("userFullSearch")
                .pathsToMatch("/api/search/**")
                .build();
    }

    @Bean
    public GroupedOpenApi rankApi() {
        return GroupedOpenApi.builder()
                .group("Rank")
                .pathsToMatch("/api/rank/**")
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
