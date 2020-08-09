package net.javaci.bank.api.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import net.javaci.bank.api.dto.EmployeeListDto;
import net.javaci.bank.api.dto.EmployeeSaveDto;
import net.javaci.bank.db.dao.EmployeeDao;
import net.javaci.bank.db.model.Employee;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private ModelMapper modelMapper;

	@GetMapping("/list")
	@ResponseBody
	public List<EmployeeListDto> listAll() {
		return employeeDao.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
	}

	@PostMapping("/add")
	@ResponseBody
	public Long add(@RequestBody EmployeeSaveDto employeeSaveDto, BindingResult bindingResult) {
		Employee employee = employeeDao.save(convertToEntity(employeeSaveDto));
		return employee.getId();
	}

	@PostMapping("/update/{id}")
	@ResponseBody
	public boolean update(@RequestBody EmployeeSaveDto employeeSaveDto, @PathVariable("id") Long id) {
		final Optional<Employee> employeeOptional = employeeDao.findById(id);
		if (!employeeOptional.isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee couldnt found by id: " + id);
		}

		Employee employee = convertToEntity(employeeSaveDto);
		
		employee.setId(employeeOptional.get().getId());
		
		employeeDao.save(employee);

		return true;
	}

	/* --------------------------------------------- */
	/* HELPER METHOD(S) */
	/* --------------------------------------------- */

	private EmployeeListDto convertToDto(Employee entity) {
		return modelMapper.map(entity, EmployeeListDto.class);
	}
	
	private Employee convertToEntity(EmployeeSaveDto employeeSaveDto) {
		return modelMapper.map(employeeSaveDto, Employee.class);
	}
}
