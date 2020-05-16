package net.javaci.bank.api.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import net.javaci.bank.api.dto.EmployeeDto;
import net.javaci.bank.api.dto.EmployeeAddRequestDto;
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
		return employeeDao.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
	}

	@PostMapping("/add")
	@ResponseBody
	public Long add(@RequestBody EmployeeAddRequestDto newEmployeeDto) {
		Employee employee = employeeDao.save(convertToEntity(newEmployeeDto));
		return employee.getId();
	}

	@PostMapping("/update/{id}")
	@ResponseBody
	public boolean update(@RequestBody EmployeeDto newEmployeeDto, @PathVariable("id") Long id) {
		final Optional<Employee> employeeOptional = employeeDao.findById(id);
		if (!employeeOptional.isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee couldnt found by id: " + id);
		}

		Employee employee = convertToEntity(newEmployeeDto);
		
		employee.setId(employeeOptional.get().getId());
		
		employeeDao.save(employee);

		return true;
	}

	/* --------------------------------------------- */
	/* HELPER METHOD(S) */
	/* --------------------------------------------- */

	private EmployeeDto convertToDto(Employee entity) {
		return modelMapper.map(entity, EmployeeDto.class);
	}
	
	private Employee convertToEntity(EmployeeDto employeeDto) {
		return modelMapper.map(employeeDto, Employee.class);
	}
}
