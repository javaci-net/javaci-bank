package net.javaci.bank.api.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.javaci.bank.api.dto.CustomerDto;
import net.javaci.bank.db.dao.CustomerDao;
import net.javaci.bank.db.model.Customer;

@RestController
@RequestMapping("/api/customer-info")
public class CustomerInfoController {

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/list")
    @ResponseBody
    public List<CustomerDto> listAll() {
        return customerDao.findAll()
                .stream()
                .map( this::convertToEntity )
                .collect(Collectors.toList());
    }

    @PostMapping("/add-update")
    @ResponseBody
    public CustomerDto add(@RequestBody CustomerDto newCustomerDto) {
        return convertToEntity( customerDao.save(modelMapper.map(newCustomerDto, Customer.class)) );
    }
    
    @PostMapping("/change-password")
    @ResponseBody
    public boolean changePassword(String currentPassword, String newPassword, String newPasswordRetype)  {
        return false;
    }
    
    @PostMapping("/login")
    @ResponseBody
    public CustomerDto login(String userName, String password)  {
        return null;
    }

    /* --------------------------------------------- */
    /* HELPER METHOD(S) */
    /* --------------------------------------------- */
    
    private CustomerDto convertToEntity(Customer customer) {
        return modelMapper.map(customer, CustomerDto.class);
    }
    
}
