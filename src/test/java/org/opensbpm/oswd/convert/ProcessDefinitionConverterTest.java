package org.opensbpm.oswd.convert;

import org.junit.jupiter.api.Test;
import org.opensbpm.engine.api.model.definition.ProcessDefinition;
import org.opensbpm.engine.examples.ExampleModels;
import org.opensbpm.engine.xmlmodel.ProcessModel;
import org.opensbpm.oswd.Process;
import org.opensbpm.oswd.jxpath.JXPath;
import org.opensbpm.oswd.parser.ProcessParser;
import org.springframework.data.domain.Example;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.notNullValue;
import static org.opensbpm.oswd.matchers.OswdMatchers.isSubjectName;

public class ProcessDefinitionConverterTest {

    @Test
    public void testToXml() throws Exception {
        //arrange
        InputStream dienstreiseantrag = ExampleModels.getDienstreiseantrag();
        ProcessDefinition processDefinition = new ProcessModel().unmarshal(dienstreiseantrag);

        //act
        Process process = new ProcessDefinitionConverter().convert(processDefinition);

        //assert
        assertThat(process, notNullValue());
    }
}
