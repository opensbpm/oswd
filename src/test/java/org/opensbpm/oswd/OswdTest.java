package org.opensbpm.oswd;

import org.junit.jupiter.api.Test;
import org.opensbpm.oswd.jxpath.JXPath;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.opensbpm.oswd.matchers.AttributeMatchers.isAttribute;
import static org.opensbpm.oswd.matchers.OswdMatchers.*;


public class OswdTest {

    @Test
    public void testParse() throws Exception {
        //arrange
        InputStream is = getClass().getResourceAsStream("/sample.oswd");
        InputStreamReader reader = new InputStreamReader(is);

        //act
        Process process = Oswd.parseOswd(reader);

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

//        assertThat(aTask.getBusinessObject().getAttributes(),
//                contains(
//                        isAttribute("AField", AttributeType.TEXT, true, true),
//                        isAttribute("BField", AttributeType.NUMBER, false, false)
//                )
//        );

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

    @Test
    public void testToOswd() throws Exception {
        //arrange
        InputStream is = getClass().getResourceAsStream("/sample.oswd");
        InputStreamReader reader = new InputStreamReader(is);
        Process process = Oswd.parseOswd(reader);

        //act
        String oswdContent = Oswd.toOswd(process);

        //assert
        is = getClass().getResourceAsStream("/sample.oswd");
        String result = new BufferedReader(new InputStreamReader(is))
                .lines().parallel().collect(Collectors.joining("\n"));
        assertThat(oswdContent, is(result));
    }
}
