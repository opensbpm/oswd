package org.opensbpm.oswd;

import org.junit.jupiter.api.Test;
import org.opensbpm.oswd.jxpath.JXPath;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.opensbpm.oswd.matchers.AttributeMatchers.isAttribute;
import static org.opensbpm.oswd.matchers.OswdMatchers.*;


public class OswdTest {

    @Test
    public void testParse() throws Exception {
        //arrange
        InputStream is = getClass().getResourceAsStream("/sample.oswd");

        //act
        Process process = Oswd.parseOswd(new InputStreamReader(is));

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
        String content = "" +
                "process AProcess\n" +
                " version 11\n" +
                " description \"A Description\"\n" +
                " \"A Subject\" with role \"A Role\"\n" +
                "  \"A Task\" show \"A Object\"\n" +
                "   with \"A Field\" as text required readonly\n" +
                "   with \"B Field\" as number\n" +
                "   proceed to \"B Task\"\n" +
                "  \"B Task\" send \"A Object\" to \"A Subject\"\n" +
                "   proceed to \"C Task\"\n" +
                "  \"C Task\" receive \"Object 1\" proceed to \"A Task\"\n" +
                " \"Object 2\" proceed to \"B Task\"\n" +
                "";
        Process process = Oswd.parseOswd(new StringReader(content));

        //act
        String oswdContent = Oswd.toOswd(process);

        //assert
        assertThat(oswdContent, is(content));
    }
}
