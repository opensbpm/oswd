package org.opensbpm.oswd.convert;

import org.junit.jupiter.api.Test;
import org.opensbpm.engine.api.model.definition.ProcessDefinition;
import org.opensbpm.engine.examples.ExampleModels;
import org.opensbpm.engine.xmlmodel.ProcessModel;
import org.opensbpm.oswd.*;
import org.opensbpm.oswd.Process;
import org.opensbpm.oswd.jxpath.JXPath;

import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.opensbpm.oswd.matchers.OswdMatchers.*;

public class ProcessDefinitionConverterTest {

    @Test
    public void testToProcess() throws Exception {
        //arrange
        InputStream travelRequest = ExampleModels.getBookPage103();
        ProcessDefinition processDefinition = new ProcessModel().unmarshal(travelRequest);

        //act
        Process process = new ProcessDefinitionConverter().convert(processDefinition);

        //assert
        System.out.println(Oswd.toOswd(process));
        assertThat(process, isProcess(
                "Dienstreiseantrag Seite/103",
                1,
                isDescription(notNullValue(String.class)),
                containsSubjects(
                        isSubject("Mitarbeiter", "Angestellte"),
                        isSubject("Vorgesetzter", "Abteilungsleiter"),
                        isSubject("Reisestelle", "Reisestelle")
                )
        ));

        JXPath<Process> jxPath = JXPath.of(process);
        Subject mitarbeiterSubject = jxPath.getValue(Subject.class, "subjects[name='Mitarbeiter']");
        assertThat(mitarbeiterSubject.getName(), is("Mitarbeiter"));
        assertThat(mitarbeiterSubject.getRole().getName(), is("Angestellte"));

        ShowTask showTask = jxPath.getValue(ShowTask.class, "subjects[name='Mitarbeiter']/tasks[name='DR-Antrag ausfüllen']");
        assertThat(showTask, isShowTask(
                "DR-Antrag ausfüllen",
                "DR-Antrag an Vorgesetzer senden"
        ));

        ReceiveTask receiveTask = jxPath.getValue(ReceiveTask.class, "subjects[name='Mitarbeiter']/tasks[name='Antwort von Vorgesetzter empfangen']");
        assertThat(receiveTask, isReceiveTask(
                "Antwort von Vorgesetzter empfangen",
                isMessage("Genehmigung", "DR antreten"),
                isMessage("Ablehnung", "Abgelehnt")
        ));

        SendTask sendTask = jxPath.getValue(SendTask.class, "subjects[name='Mitarbeiter']/tasks[name='DR-Antrag an Vorgesetzer senden']");
        assertThat(sendTask, isSendTask(
                "DR-Antrag an Vorgesetzer senden",
                "DR-Antrag",
                "Vorgesetzter",
                "Antwort von Vorgesetzter empfangen"
        ));
    }
}
