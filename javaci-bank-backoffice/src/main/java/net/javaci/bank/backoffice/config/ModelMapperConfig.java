package net.javaci.bank.backoffice.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.javaci.bank.backoffice.dto.EmployeeCreateDto;
import net.javaci.bank.db.model.Employee;



@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        final ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        
        modelMapper.addMappings(new PropertyMap<EmployeeCreateDto, Employee>() {
            @Override
            protected void configure() {
            	// When creating employee and changing password we set encrypted password
            	// other wise do not touch the password
            	skip(destination.getPassword());
            }
        });
        
        return modelMapper;
    }
}