package org.opensbpm.oswd.parser;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.opensbpm.oswd.*;
import org.opensbpm.oswd.Process;
import org.opensbpm.oswd.jxpath.JXPath;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.opensbpm.oswd.matchers.AttributeMatchers.containsAttributes;
import static org.opensbpm.oswd.matchers.AttributeMatchers.isScalarAttribute;
import static org.opensbpm.oswd.matchers.OswdMatchers.*;
import static java.util.Arrays.asList;

public class ProcessParserTest {

    @Test
    public void testParseOswd() throws Exception {
        //arrange
        InputStream is = getClass().getResourceAsStream("/sample.oswd");
        InputStreamReader reader = new InputStreamReader(is);

        //act
        Process process = ProcessParser.parseOswd(reader);

        //assert
        JXPath<Process> jxPath = new JXPath<>(process);

        assertThat(process.getName(), is("AProcess"));
        assertThat(process.getVersion(), is(11));
        assertThat(process.getSubjects(), contains(
                isSubjectName("A Subject")
        ));

        Subject aSubject = process.getSubjects().stream()
                .filter(s -> "A Subject".equals(s.getName()))
                .findFirst().orElseThrow();

        assertThat(aSubject, allOf(
                isSubjectName("A Subject"),
                isRoleName("A Role"),
                hasTasksSize(3)
        ));

        ShowTask aTask = jxPath.getValue(ShowTask.class, "subjects[name='A Subject']/tasks[name='A Task']");
        assertThat(aTask, allOf(
                isTaskName("A Task"),
                isObjectName("A Object"),
                isShowProceedTo("B Task")
        ));


        assertThat(aTask.getBusinessObject().getAttributes(), containsAttributes(
                        (Matcher<? super Attribute>) isScalarAttribute("A Field", AttributeType.TEXT, true, true),
                        (Matcher<? super Attribute>) isScalarAttribute("B Field", AttributeType.NUMBER, false, false)
                )
        );

        SendTask bTask = jxPath.getValue(SendTask.class, "subjects[name='A Subject']/tasks[name='B Task']");
        assertThat(bTask, allOf(
                isTaskName("B Task"),
                isObjectNameReference("A Object"),
                isReceiverSubjectName("A Subject"),
                isSendProceedTo("C Task")
        ));

        ReceiveTask cTask = jxPath.getValue(ReceiveTask.class, "subjects[name='A Subject']/tasks[name='C Task']");
        assertThat(cTask, allOf(
                isTaskName("C Task"),
                containsMessages(
                        isMessage("Object 1", "A Task"),
                        isMessage("Object 2", "B Task")
                )
        ));

    }
}
