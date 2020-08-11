package net.javaci.bank.api.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.javaci.bank.api.dto.AccountListDto;
import net.javaci.bank.db.model.Account;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        final ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        /*
        modelMapper.addMappings(new PropertyMap<Account, AccountAddRequestDto>() {
            @Override
            protected void configure() {
                map().setCustomer(source.getCustomer().getId());
            }
        });
        */
        modelMapper.addMappings(new PropertyMap<Account, AccountListDto>() {
            @Override
            protected void configure() {
                map().setCustomerId(source.getCustomer().getId());
            }
        });
        return modelMapper;
    }
}