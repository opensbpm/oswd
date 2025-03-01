package org.opensbpm.oswd;

import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Test;

import java.util.*;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


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
                "  ATask show Object\n" +
                "   with Field as required readonly\n" +
                "   proceed to Task\n" +
                "  BTask send Object to Subject\n" +
                "   proceed to Task\n" +
                "  CTask receive Object1 proceed to Task\n" +
                "   Object2 proceed to Task\n" +
                "";

        //act
        Process process = ProcessParser.parseProcess(content);

        //assert

        assertThat(process.getName(), is("AProcess"));
        assertThat(process.getVersion(), is(11));
        assertThat(process.getSubjects(), contains(
                isSubject(
                        isName("ASubject"),
                        isRoleName("ARole"),
                        containsTasks(
                                isTask(
                                        isA(ShowTask.class),
                                        isTaskName("ATask")
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
                )
        ));
    }


    private static CustomTypeSafeMatcher<Subject> isSubject(Matcher<? super Subject> matcher, Matcher<? super Subject>... additionals) {
        ArrayList<Matcher<? super Subject>> matchers = new ArrayList<>(List.of(matcher));
        matchers.addAll(asList(additionals));

        StringDescription description = new StringDescription();
        allOf(matchers).describeTo(description);
        return new CustomTypeSafeMatcher<>("subjects " + description.toString()) {
            @Override
            protected boolean matchesSafely(Subject subject) {
                return allOf(matchers).matches(subject);
            }
        };
    }

    private static CustomTypeSafeMatcher<Subject> isName(String name) {
        return new CustomTypeSafeMatcher<>("subject with name " + name) {
            @Override
            protected boolean matchesSafely(Subject subject) {
                return is(name).matches(subject.getName());
            }
        };
    }

    private static CustomTypeSafeMatcher<Subject> isRoleName(String name) {
        return new CustomTypeSafeMatcher<>("subject with role name " + name) {
            @Override
            protected boolean matchesSafely(Subject subject) {
                return is(name).matches(subject.getRole().getName());
            }
        };
    }

    private static CustomTypeSafeMatcher<Subject> containsTasks(Matcher<? super Task> matcher, Matcher<? super Task>... additionals) {
        ArrayList<Matcher<? super Task>> matchers = new ArrayList<>(List.of(matcher));
        matchers.addAll(asList(additionals));

        StringDescription description = new StringDescription();
        allOf(matchers).describeTo(description);
        return new CustomTypeSafeMatcher<>("tasks " + description.toString()) {
            @Override
            protected boolean matchesSafely(Subject subject) {
                return contains(matchers).matches(subject.getTasks());
            }
        };
    }

    private static CustomTypeSafeMatcher<Task> isTask(Matcher<? super Task> matcher, Matcher<? super Task>... additionals) {
        ArrayList<Matcher<? super Task>> matchers = new ArrayList<>(List.of(matcher));
        matchers.addAll(asList(additionals));

        StringDescription description = new StringDescription();
        allOf(matchers).describeTo(description);
        return new CustomTypeSafeMatcher<>("tasks " + description.toString()) {
            @Override
            protected boolean matchesSafely(Task task) {
                return allOf(matchers).matches(task);
            }
        };
    }

    private static CustomTypeSafeMatcher<Task> isTaskName(String name) {
        return new CustomTypeSafeMatcher<>("task with name " + name) {
            @Override
            protected boolean matchesSafely(Task task) {
                return is(name).matches(task.getName());
            }
        };
    }


}
