package org.opensbpm.oswd;

import org.junit.jupiter.api.Test;
import org.opensbpm.oswd.jxpath.JXPath;

import java.io.StringReader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.opensbpm.oswd.matchers.AttributeMatchers.isAttribute;
import static org.opensbpm.oswd.matchers.OswdMatchers.*;


public class OswdTest {

    @Test
    public void testParse() throws Exception {
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
                "  CTask receive Object1 proceed to ATask\n" +
                "   Object2 proceed to BTask\n" +
                "";
        System.out.println(content);

        //act
        Process process = Oswd.parseOswd(new StringReader(content));

        //assert
        JXPath<Process> jxPath = new JXPath<>(process);

        assertThat(process.getName(), is("AProcess"));
        assertThat(process.getVersion(), is(11));
        assertThat(process.getSubjects(), contains(
                isSubjectName("ASubject")
        ));

        Subject aSubject = process.getSubjects().stream()
                .filter(s -> "ASubject".equals(s.getName()))
                .findFirst().orElseThrow();

        assertThat(aSubject, allOf(
                isSubjectName("ASubject"),
                isRoleName("ARole"),
                hasTasksSize(3)
        ));

        ShowTask aTask = jxPath.getValue(ShowTask.class, "subjects[name='ASubject']/tasks[name='ATask']");
        assertThat(aTask, allOf(
                isTaskName("ATask"),
                isObjectName("AObject"),
                isShowProceedTo("BTask")
        ));

        assertThat(aTask.getBusinessObject().getAttributes(),
                contains(
                        isAttribute("AField", AttributeType.TEXT, true, true),
                        isAttribute("BField", AttributeType.NUMBER, false, false)
                )
        );

        SendTask bTask = jxPath.getValue(SendTask.class, "subjects[name='ASubject']/tasks[name='BTask']");
        assertThat(bTask, allOf(
                isTaskName("BTask"),
                isObjectNameReference("AObject"),
                isReceiverSubjectName("ASubject"),
                isSendProceedTo("CTask")
        ));

        ReceiveTask cTask = jxPath.getValue(ReceiveTask.class, "subjects[name='ASubject']/tasks[name='CTask']");
        assertThat(cTask, allOf(
                isTaskName("CTask"),
                containsMessages(
                        isMessage("Object1", "ATask"),
                        isMessage("Object2", "BTask")
                )
        ));

    }

    @Test
    public void testToOswd() throws Exception {
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
                "  CTask receive Object1 proceed to ATask\n" +
                " Object2 proceed to BTask\n" +
                "";
        Process process = Oswd.parseOswd(new StringReader(content));

        //act
        String oswdContent = Oswd.toOswd(process);

        //assert
        assertThat(oswdContent, is(content));

    }

}
