package com.availity.assessment.service;

import java.io.IOException;
import java.util.List;

public interface FileParsingService {
    List<String> parse() throws IOException;
}
