package org.opensbpm.oswd.convert;

import org.junit.jupiter.api.Test;
import org.opensbpm.engine.api.model.definition.ProcessDefinition;
import org.opensbpm.engine.examples.ExampleModels;
import org.opensbpm.engine.xmlmodel.ProcessModel;
import org.opensbpm.oswd.*;
import org.opensbpm.oswd.Process;
import org.opensbpm.oswd.jxpath.JXPath;
import org.opensbpm.oswd.parser.ProcessParser;
import org.springframework.data.domain.Example;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.opensbpm.oswd.matchers.OswdMatchers.isSubjectName;

public class ProcessDefinitionConverterTest {

    @Test
    public void testToProcess() throws Exception {
        //arrange
        InputStream dienstreiseantrag = ExampleModels.getDienstreiseantrag();
        ProcessDefinition processDefinition = new ProcessModel().unmarshal(dienstreiseantrag);

        //act
        Process process = new ProcessDefinitionConverter().convert(processDefinition);

        //assert
        assertThat(process, notNullValue());
        assertThat(process.getName(), is("Erweiterter Dienstreiseantrag"));
        assertThat(process.getVersion(), is(1));
        assertThat(process.getDescription(), notNullValue());
        assertThat(process.getSubjects(), hasSize(3));

        JXPath<Process> jxPath = new JXPath<>(process);
        Subject mitarbeiterSubject = jxPath.getValue(Subject.class, "subjects[name='Mitarbeiter']");
        assertThat(mitarbeiterSubject.getName(), is("Mitarbeiter"));
        assertThat(mitarbeiterSubject.getRole().getName(), is("Angestellte"));

        ShowTask showTask = jxPath.getValue(ShowTask.class, "subjects[name='Mitarbeiter']/tasks[name='DR-Antrag ausfüllen']");
        assertThat(showTask.getName(), is("DR-Antrag ausfüllen"));

        ReceiveTask receiveTask = jxPath.getValue(ReceiveTask.class, "subjects[name='Mitarbeiter']/tasks[name='Buchung von Reisestelle empfangen']");
        assertThat(receiveTask.getName(), is("Buchung von Reisestelle empfangen"));

        SendTask sendTask = jxPath.getValue(SendTask.class, "subjects[name='Mitarbeiter']/tasks[name='DR-Antrag an Vorgesetzer senden']");
        assertThat(sendTask.getName(), is("DR-Antrag an Vorgesetzer senden"));
    }
}
