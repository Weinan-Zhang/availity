package com.availity.assessment.service.mapper.impl;

import com.availity.assessment.service.dto.UserDto;
import com.availity.assessment.service.mapper.CSVMapper;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import static com.availity.assessment.service.Constants.*;

@Component
public class CSVRecordToUserDtoMapperImpl implements CSVMapper {

    @Override
    public UserDto map(CSVRecord record) {
        return UserDto.builder()
                .userId(record.get(CSV_HEADER_USER_ID))
                .firstName(record.get(CSV_HEADER_FIRST_NAME))
                .lastName(record.get(CSV_HEADER_LAST_NAME))
                .version(Integer.valueOf(record.get(CSV_HEADER_VERSION)))
                .insuranceCompany(record.get(CSV_HEADER_INSURANCE_COMPANY))
                .build();
    }
}
