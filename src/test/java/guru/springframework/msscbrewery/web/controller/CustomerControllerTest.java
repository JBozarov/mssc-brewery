package guru.springframework.msscbrewery.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.msscbrewery.service.CustomerService;
import guru.springframework.msscbrewery.web.model.CustomerDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;


import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @MockBean
    CustomerService customerService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    CustomerDto validCustomerDto;

    @Before
    public void setUp() {
        validCustomerDto = CustomerDto
                .builder()
                .id(UUID.randomUUID())
                .name("Customer1")
                .build();
    }

    @Test
    public void testGetCustomerById() throws Exception {
        given(customerService.getCustomerById(any(UUID.class))).willReturn(validCustomerDto);

        mockMvc.perform(get("/api/v1/customer/" + validCustomerDto.getId().toString()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(validCustomerDto.getId().toString())))
                .andExpect(jsonPath("$.name", is("Customer1")));
    }

    @Test
    public void testHandlePost() throws Exception {
        CustomerDto customerDto = validCustomerDto;
        customerDto.setId(null);
        CustomerDto savedCustomerDto = CustomerDto.builder().id(UUID.randomUUID()).name("New Customer").build();
        String customerDtoJson = objectMapper.writeValueAsString(customerDto);

        given(customerService.saveCustomer(any())).willReturn(savedCustomerDto);

        mockMvc.perform(post("/api/v1/customer/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerDtoJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void updateCustomer() throws Exception {
        CustomerDto customerDto = validCustomerDto;
        customerDto.setId(null);
        String customerDtoJson = objectMapper.writeValueAsString(customerDto);

        mockMvc.perform(put("/api/v1/customer/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerDtoJson))
                .andExpect(status().isNoContent());

        then(customerService).should().updateCustomer(any(), any());
    }

    @Test
    public void deleteCustomer() throws Exception {
        CustomerDto customerDto = validCustomerDto;
        String randomId = UUID.randomUUID().toString();
        mockMvc.perform(delete("/api/v1/customer/" + randomId)
                .param("id", randomId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}