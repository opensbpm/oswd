package org.opensbpm.oswd;

import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Test;
import org.opensbpm.oswd.parser.ProcessParser;

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
                "  ATask show AObject\n" +
                "   with AField as text required readonly\n" +
//                "   with BField as number\n" +
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
                isObjectName("AObject")
        ));

        assertThat(aTask.getBusinessObject().getAttributes(),
                contains(
                        allOf(
                                isAttribute("AField"),
                                isAttributeType(AttributeType.TEXT),
                                isRequired(true),
                                isReadonly(true)
//                        ),
//                        allOf(
//                                isAttribute("BField"),
//                                isAttributeType(AttributeType.NUMBER),
//                                isRequired(false),
//                                isReadonly(false)
                        )
                )
        );

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

    private static CustomTypeSafeMatcher<Subject> isSubjectName(String name) {
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

    private static CustomTypeSafeMatcher<? super ShowTask> isObjectName(String name) {
        return new CustomTypeSafeMatcher<>("Object with name " + name) {
            @Override
            protected boolean matchesSafely(ShowTask task) {
                return is(name).matches(task.getBusinessObject().getName());
            }
        };
    }

    private static CustomTypeSafeMatcher<Attribute> isAttribute(String name) {
        return new CustomTypeSafeMatcher<>("Attribute with name " + name) {
            @Override
            protected boolean matchesSafely(Attribute attribute) {
                return is(name).matches(attribute.getName());
            }
        };
    }

    private static CustomTypeSafeMatcher<Attribute> isAttributeType(AttributeType attributeType) {
        return new CustomTypeSafeMatcher<>("Attribute with type " + attributeType) {
            @Override
            protected boolean matchesSafely(Attribute attribute) {
                return is(attributeType).matches(attribute.getAttributeType());
            }
        };
    }

    private static CustomTypeSafeMatcher<Attribute> isRequired(boolean required) {
        return new CustomTypeSafeMatcher<>("Attribute with required " + required) {
            @Override
            protected boolean matchesSafely(Attribute attribute) {
                return is(required).matches(attribute.isRequired());
            }
        };
    }

    private static CustomTypeSafeMatcher<Attribute> isReadonly(boolean readonly) {
        return new CustomTypeSafeMatcher<>("Attribute with readonly " + readonly) {
            @Override
            protected boolean matchesSafely(Attribute attribute) {
                return is(readonly).matches(attribute.isReadonly());
            }
        };
    }

}
