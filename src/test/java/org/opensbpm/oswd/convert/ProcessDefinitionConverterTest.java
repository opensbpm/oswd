package org.opensbpm.oswd.convert;

import org.junit.jupiter.api.Test;
import org.opensbpm.engine.api.model.definition.ProcessDefinition;
import org.opensbpm.engine.examples.ExampleModels;
import org.opensbpm.engine.xmlmodel.ProcessModel;
import org.opensbpm.oswd.*;
import org.opensbpm.oswd.Process;
import org.opensbpm.oswd.jxpath.JXPath;

import java.io.InputStream;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.opensbpm.engine.utils.StreamUtils.filterToOne;
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

        Subject mitarbeiterSubject = filterToOne(process.getSubjects(), subject -> "Mitarbeiter".equals(subject.getName()))
                .orElseThrow();
        assertThat(mitarbeiterSubject, isSubject("Mitarbeiter", "Angestellte", hasTasksSize(6)));

        ShowTask showTask = filterToOne(mitarbeiterSubject.getTasks(), task -> "DR-Antrag ausfüllen".equals(task.getName()))
                .map(ShowTask.class::cast)
                .orElseThrow();
        assertThat(showTask, isShowTask(
                "DR-Antrag ausfüllen",
                "DR-Antrag an Vorgesetzter senden"
        ));

        ReceiveTask receiveTask = filterToOne(mitarbeiterSubject.getTasks(), task -> "Antwort von Vorgesetzter empfangen".equals(task.getName()))
                .map(ReceiveTask.class::cast)
                .orElseThrow();
        assertThat(receiveTask, isReceiveTask(
                "Antwort von Vorgesetzter empfangen",
                isMessage("Genehmigung", "DR antreten"),
                isMessage("Ablehnung", "Abgelehnt")
        ));

        SendTask sendTask = filterToOne(mitarbeiterSubject.getTasks(), task -> "DR-Antrag an Vorgesetzter senden".equals(task.getName()))
                .map(SendTask.class::cast)
                .orElseThrow();
        assertThat(sendTask, isSendTask(
                "DR-Antrag an Vorgesetzter senden",
                "DR-Antrag",
                "Vorgesetzter",
                "Antwort von Vorgesetzter empfangen"
        ));
    }
}
