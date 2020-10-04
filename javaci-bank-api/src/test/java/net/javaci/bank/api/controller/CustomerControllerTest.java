package net.javaci.bank.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import net.javaci.bank.api.config.JwtConfig;
import net.javaci.bank.api.dto.CustomerSaveDto;
import net.javaci.bank.api.helper.JwtConstants;
import net.javaci.bank.api.jwt.JwtUserPassAuthFilter;
import net.javaci.bank.db.dao.CustomerDao;
import net.javaci.bank.db.model.Customer;
import net.javaci.bank.db.model.enumaration.CustomerStatusType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {

	@Autowired private JwtConfig jwtConfig;
	@Autowired private MockMvc mockMvc;

	@Autowired
	private CustomerController controller;

	@MockBean
	private CustomerDao customerDao;

	private String mockJwtToken() {
		return JwtConstants.BEARER_PREFIX + " " + JwtUserPassAuthFilter.createJwtToken("john", Collections.emptySet(), jwtConfig);
	}
	@Test
	public void contextLoads() throws Exception {
		assertThat(controller).isNotNull();
	}

	@Test
	public void testListRequest() throws Exception {
		// Given
		List<Customer> customerList = new ArrayList<>();
		customerList.add(Customer.builder().id(1L)
				.citizenNumber("0001111")
				.birthDate(LocalDate.parse("1980-10-01"))
				.password("secretPassword")
				.status(CustomerStatusType.ACTIVE)
				.build());
		customerList.add(Customer.builder().id(2L)
				.citizenNumber("0002222")
				.status(CustomerStatusType.INACTIVE)
				.build());
		Mockito.when(customerDao.findAll()).thenReturn(customerList);

		// When
		ResultActions perform = this.mockMvc
				.perform(
						get(CustomerController.BASE_URL + "/list")
								.header(JwtConstants.AUTHORIZATION, mockJwtToken())
				);

		// Then
		perform
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].id", comparesEqualTo(1)))
				.andExpect(jsonPath("$[0].citizenNumber", comparesEqualTo("0001111")))
				.andExpect(jsonPath("$[0].password").doesNotExist()) // listelemede password gonderilmez
				// TODO: korayg - LoclaDate i Validateedemedim
				//.andExpect(jsonPath("$[0].birthDate", comparesEqualTo("[1980,10,1]")))
				//.andExpect(jsonPath("$[0].birthDate", arrayContaining(1980,10,1)))
				//.andExpect(jsonPath("$[0].birthDate", arrayContaining("1980,10,1")))
				.andExpect(jsonPath("$[0].status", comparesEqualTo("ACTIVE"))) // active user'largelmeli

				.andExpect(jsonPath("$[1].id", comparesEqualTo(2)))
				.andExpect(jsonPath("$[1].citizenNumber", comparesEqualTo("0002222")))
				.andExpect(jsonPath("$[1].password").doesNotExist())
				.andExpect(jsonPath("$[1].birthDate").isEmpty())
				.andExpect(jsonPath("$[1].status", comparesEqualTo("INACTIVE"))); // inactive user'lar da gelmeli

/*
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

 */
	}

	@Test
	public void registerSuccessfully() throws Exception {
		// Given
		final Long createdId = 9999L;
		Mockito.when(customerDao.findByCitizenNumber(any())).thenReturn(Optional.empty());
		Mockito.when(customerDao.save(any())).thenReturn(Customer.builder().id(createdId).build());

		// When
		CustomerSaveDto customerSaveDto = new CustomerSaveDto();
		customerSaveDto.setCitizenNumber("1");
		customerSaveDto.setPassword("mySecret");

		ResultActions perform = this.mockMvc
				.perform(
						post(CustomerController.BASE_URL + "/register")
								.header(JwtConstants.AUTHORIZATION, mockJwtToken())
								.content(new ObjectMapper().writeValueAsString(customerSaveDto))
								.contentType(MediaType.APPLICATION_JSON)
				);

		// Then
		MvcResult result = perform
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		assertThat(result.getResponse().getContentAsString()).isEqualTo("" + createdId);
	}

	@Test
	public void registerWithDuplicateCitizenNumber() throws Exception {
        // Given
        final String citizenNumber = "0001111";
        Mockito.when(customerDao.findByCitizenNumber(citizenNumber)).thenReturn(
			Optional.of(
				Customer.builder().id(1L)
                    .citizenNumber(citizenNumber)
                    .birthDate(LocalDate.parse("1980-10-01"))
                    .password("secretPassword")
                    .status(CustomerStatusType.ACTIVE)
                    .build()
			)
		);

		// When
        ResultActions perform = this.mockMvc
                .perform(
                        post(CustomerController.BASE_URL + "/register")
                                .header(JwtConstants.AUTHORIZATION, mockJwtToken())
                                .content(new Gson().toJson(Customer.builder().citizenNumber(citizenNumber)))
								.contentType(MediaType.APPLICATION_JSON)
                );

        // Then
        perform
                .andDo(print())
                .andExpect(status().isBadRequest());
	}


}