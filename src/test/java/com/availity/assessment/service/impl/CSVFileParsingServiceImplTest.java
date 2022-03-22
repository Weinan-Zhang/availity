package com.availity.assessment.service.impl;

import com.availity.assessment.service.mapper.impl.CSVRecordToUserDtoMapperImpl;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig
@SpringBootTest(properties = { "csv.default.save.root=src/test/resources/csv/" })
public class CSVFileParsingServiceImplTest {
    @Value("${csv.default.file}") String fileToparse;
    @Value("${csv.default.save.root}") String rootToSave;
    CSVFileParsingServiceImpl csvFileParsingService;

    @Autowired
    CSVRecordToUserDtoMapperImpl csvMapper;

    @BeforeEach
    public void setup() {
        this.csvFileParsingService = new CSVFileParsingServiceImpl(fileToparse, rootToSave, csvMapper);
    }

    @Test
    public void shouldStoreParsedFilesAndReturnFileList() throws IOException {
        List<String> parsedFileList = this.csvFileParsingService.parse();
        assertEquals(5, parsedFileList.size());
        List<String> lines = FileUtils.readLines(new File(parsedFileList.get(1)), StandardCharsets.UTF_8);
        assertEquals(3, lines.size());
        assertTrue("6".equals(lines.get(1).split(",")[0]));
        assertTrue("Murray".equals(lines.get(1).split(",")[2]));
        assertTrue("Walker".equals(lines.get(2).split(",")[2]));
    }
}
