package com.enummapper.after;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public EnumMapper enumMapper() {
        EnumMapper enumMapper = new EnumMapper();
        enumMapper.put("commissionType", EnumContract.CommissionType.class);
        enumMapper.put("commissionCutting", EnumContract.CommissionCutting.class);
        return enumMapper;
    }

}
