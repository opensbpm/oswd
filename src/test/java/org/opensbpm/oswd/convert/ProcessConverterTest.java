package org.opensbpm.oswd.convert;

import org.junit.jupiter.api.Test;
import org.opensbpm.engine.api.model.definition.ProcessDefinition;
import org.opensbpm.engine.xmlmodel.ProcessModel;
import org.opensbpm.oswd.Process;
import org.opensbpm.oswd.parser.ProcessParser;

import java.io.StringReader;
import java.io.StringWriter;

public class ProcessConverterTest {

    @Test
    public void testToXml() throws Exception {
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
        Process process = ProcessParser.parseOswd(new StringReader(content));

        //act
        ProcessDefinition processDefinition = new ProcessConverter().convert(process);

        //assert
        StringWriter stringWriter = new StringWriter();
        new ProcessModel().marshal(processDefinition, stringWriter);
        String xml = stringWriter.toString();
        System.out.println(xml);


    }
}
