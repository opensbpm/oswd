package org.opensbpm.oswd.convert;

import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Test;
import org.opensbpm.engine.api.model.definition.ProcessDefinition;
import org.opensbpm.engine.xmlmodel.ProcessModel;
import org.opensbpm.engine.xmlmodel.processmodel.ProcessType;
import org.opensbpm.oswd.ModelBuilderFactory;
import org.opensbpm.oswd.Process;
import org.opensbpm.engine.xmlmodel.ProcessModel;
import org.opensbpm.oswd.parser.OswdParser;
import org.opensbpm.oswd.parser.ProcessParser;

import java.io.StringWriter;

class ProcessConverterTest {

    @Test
    public void testToXml() throws JAXBException {
        //arrange
        String content = "" +
                "process AProcess\n" +
                " version 11\n" +
                " description ADescription\n" +
                " ASubject with role ARole\n" +
                "  ATask show AObject\n" +
                "   with AField as text required readonly\n" +
                "   with BField as number\n" +
                "   proceed to BTask\n" +
                "  BTask send AObject to ASubject\n" +
                "   proceed to CTask\n" +
                "  CTask receive AObject proceed to ATask\n" +
                "   AObject proceed to BTask\n" +
                "";
        Process process = ProcessParser.parseProcess(content);

        //act
        ProcessDefinition processDefinition = new ProcessConverter().convert(process);

        //assert
        StringWriter stringWriter = new StringWriter();
        new ProcessModel().marshal(processDefinition, stringWriter);
        String xml = stringWriter.toString();
        System.out.println(xml);


    }
}
