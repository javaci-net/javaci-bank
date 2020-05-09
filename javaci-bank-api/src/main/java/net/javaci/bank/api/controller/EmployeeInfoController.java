package net.javaci.bank.api.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.javaci.bank.api.dto.EmployeeDto;
import net.javaci.bank.db.dao.EmployeeDao;
import net.javaci.bank.db.model.Employee;

@RestController
@RequestMapping("/api/employee-info")
public class EmployeeInfoController {

    @Autowired
    private EmployeeDao employeeDao;
    
    @Autowired
    private ModelMapper modelMapper;
    
    @GetMapping("/list")
    @ResponseBody
    public List<EmployeeDto> listAll() {
        return employeeDao.findAll()
                .stream()
                .map( this::convertToEntity )
                .collect(Collectors.toList());
    }
    
    /* --------------------------------------------- */
    /* HELPER METHOD(S) */
    /* --------------------------------------------- */
    
    private EmployeeDto convertToEntity(Employee entity) {
        return modelMapper.map(entity, EmployeeDto.class);
    }
}
