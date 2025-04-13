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
   mvn clean install
   ```

## Usage
To run the application, use the following command:
   ```sh
  java -jar target/oswd-converter.jar -input <input-file> -output <output-file>
   ```


### Example
   ```sh
  java -jar target/oswd-converter.jar -input src/test/resources/sample.oswd -output sample.xml
   ```
