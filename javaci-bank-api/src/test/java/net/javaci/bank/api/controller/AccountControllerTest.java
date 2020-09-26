package net.javaci.bank.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.removeHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import net.javaci.bank.api.config.JwtConfig;
import net.javaci.bank.api.helper.JwtConstants;
import net.javaci.bank.api.jwt.JwtUserPassAuthFilter;
import net.javaci.bank.db.dao.AccountDao;
import net.javaci.bank.db.model.Account;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {

	@Autowired private JwtConfig jwtConfig;
	@Autowired private MockMvc mockMvc;
	
	@InjectMocks private AccountController controller;
	@MockBean private AccountDao accountDao;
	
	@Test
	public void contextLoads() throws Exception {
		assertThat(controller).isNotNull();
	}
	
	@Test
	public void testListRequest() throws Exception {
		
		// Given
		List<Account> accountList = new ArrayList<>();
		accountList.add(Account.builder().accountName("account-1").build());
		accountList.add(Account.builder().accountName("account-2").build());
		Mockito.when(accountDao.findAll()).thenReturn(accountList);
		String token = JwtUserPassAuthFilter.createJwtToken("john", Collections.emptySet(), jwtConfig);
		
		// When
		ResultActions perform = this.mockMvc
			.perform(
					get(AccountController.API_ACCOUNT_BASE_URL + "/list")
					.header(JwtConstants.AUTHORIZATION, JwtConstants.BEARER_PREFIX + token)
			);
		
		// Then
		perform
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$", hasSize(2)))
			.andExpect(jsonPath("$[0].accountName", containsString("account-1") ))
			.andExpect(jsonPath("$[1].accountName", containsString("account-2") ))
			.andExpect(
				content()
				.json("[\n" + 
					"  {\n" + 
					"    \"customerId\": null,\n" + 
					"    \"accountName\": \"account-1\",\n" + 
					"    \"description\": null,\n" + 
					"    \"balance\": null,\n" + 
					"    \"currency\": null,\n" + 
					"    \"status\": null,\n" + 
					"    \"id\": null,\n" + 
					"    \"accountNumber\": null\n" + 
					"  },\n" + 
					"  {\n" + 
					"    \"customerId\": null,\n" + 
					"    \"accountName\": \"account-2\",\n" + 
					"    \"description\": null,\n" + 
					"    \"balance\": null,\n" + 
					"    \"currency\": null,\n" + 
					"    \"status\": null,\n" + 
					"    \"id\": null,\n" + 
					"    \"accountNumber\": null\n" + 
					"  }\n" + 
					"]"
				)
			);
		
		// Document
		perform.andDo(
				document(
						"{class-name}/{method-name}",
						preprocessRequest(removeHeaders("Authorization")), 
			            preprocessResponse(prettyPrint()),
						responseFields(
                                fieldWithPath("[].customerId").description("Account's Customer Id").type(JsonFieldType.VARIES),
                                fieldWithPath("[].accountName").description("Account Name").type(JsonFieldType.STRING),
                                fieldWithPath("[].description").description("Account Description").type(JsonFieldType.VARIES),
                                fieldWithPath("[].balance").description("Account Balance").type(JsonFieldType.VARIES),
                                fieldWithPath("[].currency").description("Account Currency").type(JsonFieldType.VARIES),
                                fieldWithPath("[].status").description("Account Status").type(JsonFieldType.VARIES),
                                fieldWithPath("[].id").description("Account Id").type(JsonFieldType.VARIES),
                                fieldWithPath("[].accountNumber").description("Account No").type(JsonFieldType.VARIES)
                        )
				)
		);
	}
	
}
