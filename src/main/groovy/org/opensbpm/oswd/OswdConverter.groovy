package org.opensbpm.oswd;

import jakarta.xml.bind.JAXBException;
import org.opensbpm.engine.api.model.definition.ProcessDefinition;
import org.opensbpm.engine.xmlmodel.ProcessModel;

import java.nio.charset.StandardCharsets;

class OswdConverter {
    File inputFile
    File outputFile

    OswdConverter(File inputFile, File outputFile) {
        this.inputFile = Objects.requireNonNull(inputFile)
        this.outputFile = Objects.requireNonNull(outputFile)
    }

    void convert() throws JAXBException,IOException {

        ProcessDefinition processDefinition
        if(inputFile.name.endsWith(".oswd")){
            try (FileReader reader = new FileReader(inputFile, StandardCharsets.UTF_8)) {
                processDefinition = Oswd.readOswd(reader)
            }
        }else if(inputFile.name.endsWith(".xml")) {
            try (FileReader reader = new FileReader(inputFile, StandardCharsets.UTF_8)) {
                processDefinition = new ProcessModel().unmarshal(reader)
            }
        }else {
            throw new IllegalArgumentException("Unsupported file type: " + inputFile.name)
        }


        if(outputFile.name.endsWith(".oswd")){
            try (FileWriter writer = new FileWriter(outputFile)) {
                Oswd.writeProcess(processDefinition, writer)
            }
        }else if(outputFile.name.endsWith(".xml")) {
            try (FileWriter writer = new FileWriter(outputFile)) {
                new ProcessModel().marshal(processDefinition, writer)
            }
        }else {
            throw new IllegalArgumentException("Unsupported file type: " + inputFile.name)
        }
    }
}
