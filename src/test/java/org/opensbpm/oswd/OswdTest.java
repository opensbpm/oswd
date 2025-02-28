package org.opensbpm.oswd;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Test;
import org.opensbpm.oswd.OswdParser.DefinitionContext;
import org.opensbpm.oswd.OswdParser.ProcessContext;
import org.opensbpm.oswd.OswdParser.VersionContext;
import org.opensbpm.oswd.OswdParser.ProceedContext;
import org.opensbpm.oswd.ModelBuilderFactory.ProcessBuilder;

import java.sql.Array;
import java.util.*;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.opensbpm.oswd.ContextStackFactory.processItem;


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
                "  Task show Object\n" +
                "   with Field as required readonly\n" +
                "   proceed to Task\n" +
                "  Task send Object to Subject\n" +
                "   proceed to Task\n" +
                "  Task receive Object1 proceed to Task\n" +
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
                        isRoleName("ARole")
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

}
