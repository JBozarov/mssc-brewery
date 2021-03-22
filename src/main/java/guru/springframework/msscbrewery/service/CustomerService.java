package guru.springframework.msscbrewery.service;


import guru.springframework.msscbrewery.web.model.CustomerDto;

public interface CustomerService {
    CustomerDto getCustomerById(Integer customerId);
}
