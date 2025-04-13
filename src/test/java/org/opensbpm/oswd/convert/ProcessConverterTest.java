package org.opensbpm.oswd.convert;

import org.junit.jupiter.api.Test;
import org.opensbpm.engine.api.model.definition.ProcessDefinition;
import org.opensbpm.engine.api.model.definition.StateDefinition;
import org.opensbpm.engine.api.model.definition.SubjectDefinition;
import org.opensbpm.engine.xmlmodel.ProcessModel;
import org.opensbpm.oswd.Process;
import org.opensbpm.oswd.parser.ProcessParser;

import java.io.StringReader;
import java.io.StringWriter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.opensbpm.engine.api.junit.FunctionStateDefinitionMatchers.containsHeads;
import static org.opensbpm.engine.api.junit.ProcessDefinitionMatchers.*;
import static org.opensbpm.engine.api.junit.ReceiveStateDefinitionMatchers.*;
import static org.opensbpm.engine.api.junit.StateDefinitionMatchers.isState;
import static org.opensbpm.engine.api.junit.FunctionStateDefinitionMatchers.isFunctionState;
import static org.opensbpm.engine.api.junit.SendStateDefinitionMatchers.isSendState;

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

        assertThat(processDefinition, isProcessName("AProcess"));
        assertThat(processDefinition, isProcessVersion(11));
        assertThat(processDefinition.getSubjects(), hasSize(1));

        SubjectDefinition subjectDefinition = processDefinition.getSubjects().get(0);
        assertThat(subjectDefinition, isSubjectName("ASubject"));
        assertThat(subjectDefinition.getStates(), containsInAnyOrder(
                isFunctionState("ATask"),
                isSendState("BTask"),
                isReceiveState("CTask")
        ));

        StateDefinition aTaskDefinition = findTaskByName(subjectDefinition, "ATask");
        assertThat(aTaskDefinition, isFunctionState(
                "ATask",
                containsHeads(isSendState("BTask"))
        ));

        StateDefinition bTaskDefinition = findTaskByName(subjectDefinition, "BTask");
        assertThat(bTaskDefinition, isSendState(
                        "BTask",
                        "ASubject",
                        "AObject",
                        isReceiveState("CTask")
                )
        );

        StateDefinition cTaskDefinition = findTaskByName(subjectDefinition, "CTask");
        assertThat(cTaskDefinition, isReceiveState(
                "CTask",
                isMessage("AObject", "ATask"),
                isMessage("AObject", "BTask")
                )
        );
    }

    private static StateDefinition findTaskByName(SubjectDefinition subjectDefinition, String taskName) {
        return subjectDefinition.getStates().stream()
                .filter(state -> taskName.equals(state.getName()))
                .findFirst().orElseThrow();
    }
}
