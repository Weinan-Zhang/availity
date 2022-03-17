package com.availity.assessment.service.mapper;

import org.apache.commons.csv.CSVRecord;

public interface CSVMapper<T> {
    T map(CSVRecord record);
}
