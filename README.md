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
    git clone https://github.com/yourusername/oswd-converter.git
    cd oswd-converter
    ```

2. Build the project using Maven:
    ```sh
    mvn clean install
    ```

## Usage
To run the application, use the following command:
```sh
java -jar target/oswd-converter-1.0.0.jar -input <input-file> -output <output-file>
```

or with maven
```sh
mvn exec:java -Dexec.mainClass="org.opensbpm.converter.Converter" -Dexec.args="-input <input-file> -output <output-file>"
```

### Example
```sh
mvn exec:java -Dexec.mainClass="org.opensbpm.converter.Converter" -Dexec.args="-input src/test/resources/sample.oswd -output travelrequest.xml"
```
