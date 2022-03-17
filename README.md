# Availity Assessment

## Configuration
* The default CSV file to be parsed is 'static/csv/enrollment.csv'
* The default root path for parsed CSV files to be stored in is 'static/csv/'
* The default setting can be configured by modifying 'csv.default.file' and 'csv.default.save.root' configured in application.properties

## How to run the required functions
* This is a web application which runs on port 8080
* There are two endpoints for running required functions:
    * The GET request on endpoint 'http://localhost:8080/csv_parse' will parse enrollment.csv, group by company, filter out duplicates, sort by last name and then first name, and store parsed records in separate csv files; the relative path of stored files will be returned as response.
    * The GET request on endpoint 'http://localhost:8080/check_parentheses' will check three pre-defined LISP expressions and return the expression and the check result for it as response

## The core implementation for required work
* the core logic for CSV file parsing can be found in CSVFileParsingServiceImpl, the all relative and helper components can be tracked there
* the core logic for LISP parentheses check is implemented in com.availity.assessment.util.Utils.checkParentheses(String str)
