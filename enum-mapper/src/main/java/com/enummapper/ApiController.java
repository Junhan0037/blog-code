package com.enummapper;

import com.enummapper.after.EnumContract;
import com.enummapper.after.EnumMapper;
import com.enummapper.after.EnumModel;
import com.enummapper.after.EnumValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ApiController {

    @GetMapping("/enum")
    public Map<String, Object> getEnum() {
        Map<String, Object> enums = new LinkedHashMap<>();

        Class commissionType = EnumContract.CommissionType.class;
        Class commissionCutting = EnumContract.CommissionCutting.class;

        enums.put("commissionType", commissionType.getEnumConstants());
        enums.put("commissionCutting", commissionCutting.getEnumConstants());

        return enums;
    }

    @GetMapping("/value")
    public Map<String, List<EnumValue>> getEnumValue() {
        Map<String, List<EnumValue>> enumValues = new LinkedHashMap<>();

        enumValues.put("commissionType", toEnumValues(EnumContract.CommissionType.class));
        enumValues.put("commissionCutting", toEnumValues(EnumContract.CommissionCutting.class));

        return enumValues;
    }

    private List<EnumValue> toEnumValues(Class<? extends EnumModel> e){
        return Arrays
                .stream(e.getEnumConstants())
                .map(EnumValue::new)
                .collect(Collectors.toList());
    }

    private EnumMapper enumMapper;

    public ApiController(EnumMapper enumMapper) {
        this.enumMapper = enumMapper;
    }

    @GetMapping("/mapper")
    public Map<String, List<EnumValue>> getMapper() {
        return enumMapper.getAll();
    }

    @GetMapping("/models")
    public List<EnumModel> getModel() {
        return Arrays
                .stream(EnumContract.CommissionType.class.getEnumConstants())
                .collect(Collectors.toList());
    }

}
