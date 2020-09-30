package net.javaci.bank.api.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.fasterxml.classmate.TypeBindings;
import com.fasterxml.classmate.types.ResolvedObjectType;

import net.javaci.bank.api.model.UserPassAuthRequest;
import net.javaci.bank.api.model.UserPassAuthResponse;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig extends WebMvcConfigurationSupport {

    @Bean
    public Docket api() {
        SecurityReference securityReference = SecurityReference.builder()
                .reference("jwtToken")
                .scopes(new AuthorizationScope[0])
                .build();

        SecurityContext securityContext = SecurityContext.builder()
                .securityReferences(Arrays.asList(securityReference))
                .build();

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("net.javaci.bank.api"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .additionalModels(
                	ResolvedObjectType.create(UserPassAuthRequest.class, TypeBindings.emptyBindings(), null, null),
                	ResolvedObjectType.create(UserPassAuthResponse.class, TypeBindings.emptyBindings(), null, null)
                )
                .securitySchemes(Arrays.asList(apiKey()))
                .securityContexts(Arrays.asList(securityContext));
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("**").addResourceLocations("classpath:/static/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/index.html");
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Javaci-Bank REST API Document")
                .description("work in progress")
                .termsOfServiceUrl("localhost")
                .version("1.0")
                .build();
    }

    private ApiKey apiKey() {
        return new ApiKey("jwtToken", "Authorization", "header");
    }
}
