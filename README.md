# OSWD Converter

## Overview
The OSWD Converter is a Java-based application that uses ANTLR to convert OSWD files into the OpenSBPM-XML format, 
which can be executed in the OpenSBPM engine. It leverages Apache Commons CLI for command-line argument parsing.

## Requirements
- Java 11 or higher
- Maven 3.6.0 or higher

## Installation
1. Clone the repository:
   ```sh
   git clone https://github.com/opensbpm/oswd.git
   cd oswd
   ```

2. Build the project using Maven:
   ```sh
   mvn clean install site
   ```

3. (Optional) Display the generated site:
   ```sh
   mvn site:run
   ```
   Open your browser and navigate to `http://localhost:8080` to view the site.
   If you want to view the JaCoco report, navigate to `http://localhost:8080/jacoco/index.html`.

## Usage
To run the application, use the following command:
   ```sh
  java -jar target/oswd-converter.jar -input <input-file> -output <output-file>
   ```


### Example
   ```sh
  java -jar target/oswd-converter.jar -input src/test/resources/sample.oswd -output sample.xml
   ```
