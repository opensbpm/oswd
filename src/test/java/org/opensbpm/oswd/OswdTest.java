package org.opensbpm.oswd;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.Test;
import org.opensbpm.oswd.OswdParser.DefinitionContext;
import org.opensbpm.oswd.OswdParser.ProcessContext;
import org.opensbpm.oswd.OswdParser.VersionContext;
import org.opensbpm.oswd.OswdParser.ProceedContext;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;

public class OswdTest {


    @Test
    public void testAll() throws Exception {
//        InputStream resource = OswdTest.class.getResourceAsStream("/sample.oswd");
//        OswdLexer oswdLexer = new OswdLexer(CharStreams.fromStream(resource));

        String content = "" +
                "process AProcess\n" +
                " version 11\n" +
                " description ADescription\n" +
                " Subject with role Role\n" +
                "  Task show Object\n" +
                "   with Field as required readonly\n" +
                "   proceed to Task\n" +
                "  Task send Object to Subject\n" +
                "   proceed to Task\n" +
                "  Task receive Object1 proceed to Task\n" +
                "   Object2 proceed to Task\n" +
                "";
        OswdLexer oswdLexer = new OswdLexer(CharStreams.fromString(content));

        CommonTokenStream tokens = new CommonTokenStream(oswdLexer);
        OswdParser parser = new OswdParser(tokens);
        DefinitionContext process = parser.definition();

        Map<String,String> results = new HashMap<>();
        OswdBaseListener listener= new OswdBaseListener(){
            @Override
            public void enterProcess(ProcessContext ctx) {
                results.put("process", ctx.IDENTIFIER().getText());
            }

            @Override
            public void enterVersion(VersionContext ctx) {
                results.put("version", ctx.INT().getText());
            }

            @Override
            public void exitProceed(ProceedContext ctx) {
                ctx.IDENTIFIER().getText();
            }
        };
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(listener, process);

        assertThat(results, hasEntry("process", "AProcess"));
        assertThat(results, hasEntry("version", "11"));
    }
}
