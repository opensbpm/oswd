package org.opensbpm.oswd;

import org.apache.commons.jxpath.JXPathContext;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Test;
import org.opensbpm.oswd.parser.ProcessParser;

import java.util.*;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.opensbpm.oswd.matchers.AttributeMatchers.isAttribute;
import static org.opensbpm.oswd.matchers.OswdMatchers.*;


public class OswdTest {


    @Test
    public void testAll() throws Exception {
//        InputStream resource = OswdTest.class.getResourceAsStream("/sample.oswd");
//        OswdLexer oswdLexer = new OswdLexer(CharStreams.fromStream(resource));

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
        Process process = ProcessParser.parseProcess(content);

        //assert
        JXPathContext jxPathContext = JXPathContext.newContext(process);

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
                containsTasks(
                        isTask(
                                isA(ShowTask.class),
                                isTaskName("ATask"),
                                (Matcher<? super Task>) isObjectName("AObject")
                        ),
                        isTask(
                                isA(SendTask.class),
                                isTaskName("BTask")
                        ),
                        isTask(
                                isA(ReceiveTask.class),
                                isTaskName("CTask")
                        )
                )
        ));

        ShowTask aTask = aSubject.getTasks().stream()
                .filter(task -> "ATask".equals(task.getName()))
                .findFirst()
                .map(ShowTask.class::cast)
                .orElseThrow();

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

        SendTask bTask = (SendTask) jxPathContext.getValue("subjects[name='ASubject']/tasks[name='BTask']");
        assertThat(bTask, allOf(
                isTaskName("BTask"),
                isObjectNameReference("AObject"),
                isReceiverSubjectName("ASubject"),
                isSendProceedTo("CTask")
        ));

        ReceiveTask cTask = (ReceiveTask) jxPathContext.getValue("subjects[name='ASubject']/tasks[name='CTask']");
        assertThat(cTask, allOf(
                isTaskName("CTask"),
                containsMessages(
                        isMessage("Object1", "ATask"),
                        isMessage("Object2", "BTask")
                )
        ));


    }


}
