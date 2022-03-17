package com.availity.assessment.service.impl;

import com.availity.assessment.exception.ParseException;
import com.availity.assessment.service.FileParsingService;
import com.availity.assessment.service.dto.UserDto;
import com.availity.assessment.service.mapper.CSVMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.availity.assessment.service.Constants.*;

@Service
public class CSVFileParsingServiceImpl implements FileParsingService {
    private static final String[] HEADERS = { CSV_HEADER_USER_ID, CSV_HEADER_FIRST_NAME, CSV_HEADER_LAST_NAME, CSV_HEADER_VERSION, CSV_HEADER_INSURANCE_COMPANY};
    private static final String DELIMITER_COMMAS = ",";
    private static final String DASH = "-";
    private static final Pattern VERSION_VALIDATE_PATTERN = Pattern.compile("\\d+");
    private static final String INVALID_VERSION_EXCEPTION = "the record with id %s has an invalid version of %s";
    private String fileToParse;
    private String rootToSave;
    private CSVMapper csvMapper;

    public CSVFileParsingServiceImpl(@Value("${csv.default.file}") final String fileToparse,
                                     @Value("${csv.default.save.root}") final String rootToSave,
                                     final CSVMapper csvMapper){
        this.fileToParse = fileToparse;
        this.rootToSave = rootToSave;
        this.csvMapper = csvMapper;
    }

    @Override
    public List<String> parse() throws IOException {
        Reader in = new FileReader(this.fileToParse);
        List<CSVRecord> records = CSVFormat.DEFAULT
                .builder()
                .setHeader(HEADERS)
                .setDelimiter(DELIMITER_COMMAS)
                .setSkipHeaderRecord(true)
                .build()
                .parse(in)
                .getRecords();

        Map<String, CSVRecord> duplicatFilteredMap = filterDuplicateRecords(records);
        Map<String, List<UserDto>> groupedMap = groupRecordsByCompany(duplicatFilteredMap);
        Map<String, List<UserDto>> sortedMap = sortByFullName(groupedMap);
        return saveParsedCSVs(sortedMap);
    }

    private Map<String, CSVRecord> filterDuplicateRecords(List<CSVRecord> records) {
        Map<String, CSVRecord> duplicatFilteredMap = new HashMap<>();
        records.stream().forEach(record -> {
            validateRecordVersion(record);
            if(duplicatFilteredMap.containsKey(record.get(CSV_HEADER_USER_ID) + DASH + record.get(CSV_HEADER_INSURANCE_COMPANY))) {
                if(Integer.valueOf(record.get(CSV_HEADER_VERSION)) > Integer.valueOf(duplicatFilteredMap.get(record.get(CSV_HEADER_USER_ID) + DASH + record.get(CSV_HEADER_INSURANCE_COMPANY)).get(CSV_HEADER_VERSION))) {
                    duplicatFilteredMap.put(record.get(CSV_HEADER_USER_ID) + DASH + record.get(CSV_HEADER_INSURANCE_COMPANY), record);
                }
            }
            else {
                duplicatFilteredMap.put(record.get(CSV_HEADER_USER_ID) + DASH + record.get(CSV_HEADER_INSURANCE_COMPANY), record);
            }
        });
        return duplicatFilteredMap;
    }

    private void validateRecordVersion(CSVRecord record) {
        if(!VERSION_VALIDATE_PATTERN.matcher(record.get(CSV_HEADER_VERSION)).matches()) {
            throw new ParseException(String.format(INVALID_VERSION_EXCEPTION, record.get(CSV_HEADER_USER_ID), record.get(CSV_HEADER_VERSION)));
        }
    }

    private Map<String, List<UserDto>> groupRecordsByCompany(Map<String, CSVRecord> recordMapToGroup) {
        Map<String, List<UserDto>> groupedMap = new HashMap<>();
        recordMapToGroup.entrySet().stream().forEach(entry -> {
            CSVRecord record = entry.getValue();
            if(!groupedMap.containsKey(record.get(CSV_HEADER_INSURANCE_COMPANY))) {
                List<UserDto> groupedUserDtos = new ArrayList<>();
                groupedUserDtos.add((UserDto) this.csvMapper.map(record));
                groupedMap.put(record.get(CSV_HEADER_INSURANCE_COMPANY), groupedUserDtos);
            }
            else {
                groupedMap.get(record.get(CSV_HEADER_INSURANCE_COMPANY)).add((UserDto) this.csvMapper.map(record));
            }
        });
        return groupedMap;
    }

    private Map<String, List<UserDto>> sortByFullName(Map<String, List<UserDto>> mapToSort) {
        Comparator<UserDto> compareByFirstName = Comparator.comparing( UserDto::getFirstName );
        Comparator<UserDto> compareByLastName = Comparator.comparing( UserDto::getLastName );
        Comparator<UserDto> compareByFullName = compareByLastName.thenComparing(compareByFirstName);
        Map<String, List<UserDto>> sortedUserMap = new HashMap<>();
        mapToSort.entrySet().parallelStream().forEach(entry -> sortedUserMap.put(entry.getKey(), entry.getValue().stream().sorted( compareByFullName ).collect(Collectors.toList())));
        return sortedUserMap;
    }

    private List<String> saveParsedCSVs(Map<String, List<UserDto>> mapToSave) throws IOException {
        List<String> savedFiles = new ArrayList<>();
        mapToSave.entrySet().parallelStream().forEach(entry -> {
            savedFiles.add(rootToSave + entry.getKey() + ".csv");
            try (
                    FileWriter out = new FileWriter(rootToSave + entry.getKey() + ".csv");
                    CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT
                    .builder().setHeader(HEADERS).build())
            ) {
                entry.getValue().forEach(userDto -> {
                    try {
                        printer.printRecord(userDto.getUserId(), userDto.getFirstName(), userDto.getLastName(), userDto.getVersion(), userDto.getInsuranceCompany());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return savedFiles;
    }
}
