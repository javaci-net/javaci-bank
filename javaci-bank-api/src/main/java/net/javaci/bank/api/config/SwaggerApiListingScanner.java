package net.javaci.bank.api.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import com.fasterxml.classmate.TypeResolver;

import lombok.extern.slf4j.Slf4j;
import net.javaci.bank.api.model.UserPassAuthRequest;
import springfox.documentation.builders.OperationBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ApiListingScannerPlugin;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
@Slf4j
public class SwaggerApiListingScanner implements ApiListingScannerPlugin {

	// tag::api-listing-plugin[]
    private final CachingOperationNameGenerator operationNames;

    /**
     * @param operationNames - CachingOperationNameGenerator is a component bean
     *                       that is available to be autowired
     */
    public SwaggerApiListingScanner(CachingOperationNameGenerator operationNames) {//<9>
        this.operationNames = operationNames;
    }

    @Override
    public List<ApiDescription> apply(DocumentationContext context) {
        return new ArrayList<>(
                Arrays.asList(
                        new ApiDescription(null, "/login", "login", Collections.singletonList(
                                new OperationBuilder(operationNames)
                                        .summary("login")
                                        // .tags(Set.of("jwt-authentication-filter"))
                                        // FIXME Set.of 1.9 da geldigi icin heroku da depley hatasi veriyor ilginc bir sekilde localde vermiyor???
                                        .tags(new HashSet<>(Arrays.asList("jwt-authentication-filter")))
                                        .authorizations(new ArrayList<>())
                                        .position(1)
                                        .codegenMethodNameStem("loginPost")
                                        .method(HttpMethod.POST)
                                        .notes("This is a login method")
                                        .parameters(
                                                Arrays.asList(
                                                        new ParameterBuilder()
                                                                .description("Login Parameter")
                                                                .type(new TypeResolver().resolve(UserPassAuthRequest.class))
                                                                .name("authReq")
                                                                .parameterType("body")
                                                                .parameterAccess("access")
                                                                .required(true)
                                                                .modelRef(new ModelRef("UserPassAuthRequest"))
                                                                .build()
                                                )
                                        ).responseMessages(responseMessages())
                                        .responseModel(new ModelRef(("UserPassAuthResponse")))
                                        .build()
                        ), false)));
    }

    /**
     * @return Set of response messages that override the default/global response messages
     */
    private Set<ResponseMessage> responseMessages() {
    	
        ResponseMessage code200 = new ResponseMessageBuilder()
                        .code(200)
                        .responseModel(new ModelRef("UserPassAuthResponse"))
                        .build();
        
        /*--
		ResponseMessage code401 = new ResponseMessageBuilder()
                        .code(401)
                        .responseModel(new ModelRef("ApiError"))
                        .build();
		
		ResponseMessage code403 = new ResponseMessageBuilder()
		        .code(403)
		        .responseModel(new ModelRef("ApiError"))
		        .build();
		
		ResponseMessage code404 = new ResponseMessageBuilder()
		        .code(404)
		        .responseModel(new ModelRef("ApiError"))
		        .build();
        */
        
		return new HashSet<>(Arrays.asList(code200/*, code401, code403, code404*/ ));
    }
    // tag::api-listing-plugin[]

    @Override
    public boolean supports(DocumentationType delimiter) {
        return DocumentationType.SWAGGER_2.equals(delimiter);
    }

}
