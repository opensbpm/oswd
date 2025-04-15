package org.opensbpm.oswd

import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Test
import org.opensbpm.engine.api.model.ProcessModelState
import org.opensbpm.engine.api.model.definition.PermissionDefinition.Permission
import org.opensbpm.engine.api.model.definition.ProcessDefinition
import org.opensbpm.oswd.model.Process

import static org.hamcrest.CoreMatchers.not
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*
import static org.opensbpm.engine.api.junit.FunctionStateDefinitionMatchers.*
import static org.opensbpm.engine.api.junit.ModelUtils.getSubject
import static org.opensbpm.engine.api.junit.ProcessDefinitionMatchers.isObjectName
import static org.opensbpm.engine.api.junit.ProcessDefinitionMatchers.isSubjectName
import static org.opensbpm.engine.api.junit.ReceiveStateDefinitionMatchers.isMessage
import static org.opensbpm.engine.api.junit.ReceiveStateDefinitionMatchers.isReceiveState
import static org.opensbpm.engine.api.junit.SendStateDefinitionMatchers.isSendState

class FreemarkerTest {

    @Test
    public void testParseOswd() throws Exception {
        //arrange
        InputStream inputStream = getClass().getResourceAsStream("/sample.oswd");
        InputStreamReader reader = new InputStreamReader(inputStream);
        Process process = Oswd.readProcess(reader)

        //act
        Writer writer = new StringWriter()
        Freemarker.write(process, writer);
        writer.close()
        //printf "%s", writer.toString()
        ProcessDefinition result = Oswd.readOswd(new StringReader(writer.toString()))


        //assert
        MatcherAssert.assertThat(result.getName(), is("Dienstreiseantrag"));
        assertThat(result.getVersion(), is(1));
        assertThat(result.getState(), is(ProcessModelState.ACTIVE));
        assertThat(result.getDescription(), not(emptyString()));
        assertThat("wrong subjects", result.getSubjects(),
                containsInAnyOrder(
                        isSubjectName("Mitarbeiter"),
                        isSubjectName("Vorgesetzter"),
                        isSubjectName("Reisestelle")
                ));

        assertThat("wrong states for 'Mitarbeiter' ", getSubject(result, "Mitarbeiter").getStates(),
                containsInAnyOrder(isFunctionState("DR-Antrag ausfüllen",
                        containsPermisssions(isPermission("DR-Antrag",
                                isFieldPermission("Name", Permission.WRITE, true),
                                isFieldPermission("Reisebeginn", Permission.WRITE, true),
                                isFieldPermission("Reiseende", Permission.WRITE, true),
                                isFieldPermission("Reiseziel", Permission.WRITE, true)
                        )
                        ),
                        containsHeads(isSendState("DR-Antrag an Vorgesetzter senden"))
                ),
                        isSendState("DR-Antrag an Vorgesetzter senden", "Vorgesetzter", "DR-Antrag", "Antwort von Vorgesetzter empfangen"),
                        isReceiveState("Antwort von Vorgesetzter empfangen", isMessage("Genehmigung", "DR antreten"), isMessage("Ablehnung", "Abgelehnt")),
                        isFunctionState("DR antreten", containsHeads(isFunctionState("DR beendet"))),
                        isFunctionState("DR beendet"),
                        isFunctionState("Abgelehnt")
                ));

        assertThat("wrong states for 'Vorgesetzter' ", getSubject(result, "Vorgesetzter").getStates(),
                containsInAnyOrder(
                        isReceiveState("DR-Antrag empfangen", isMessage("DR-Antrag", "DR-Antrag prüfen")),
                        isFunctionState("DR-Antrag prüfen", containsHeads(
                                isSendState("Genehmigen"),
                                isSendState("Ablehnen")
                        )),
                        isSendState("Genehmigen", "Mitarbeiter", "Genehmigung", "Buchung veranlassen"),
                        isSendState("Buchung veranlassen", "Reisestelle", "DR-Antrag", "Ende"),
                        isSendState("Ablehnen", "Mitarbeiter", "Ablehnung",
                                isFunctionState("Ende")
                        ),
                        isFunctionState("Ende")
                ));

        assertThat("wrong states for 'Reisestelle' ", getSubject(result, "Reisestelle").getStates(),
                containsInAnyOrder(
                        isReceiveState("DR-Antrag empfangen", isMessage("DR-Antrag", "Buchen")),
                        isFunctionState("Buchen", containsHeads(isFunctionState("Reise gebucht"))),
                        isFunctionState("Reise gebucht")
                ));

        assertThat("wrong objects", result.getObjects(),
                containsInAnyOrder(
                        isObjectName("DR-Antrag"),
                        isObjectName("Genehmigung"),
                        isObjectName("Ablehnung")
                ));

    }


}
