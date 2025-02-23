package org.opensbpm.dsl;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.Test;
import org.opensbpm.dsl.OswdParser.DefinitionContext;
import org.opensbpm.dsl.OswdParser.ProcessContext;
import org.opensbpm.dsl.OswdParser.VersionContext;
import java.io.InputStream;
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
                "process Xyz\n" +
                " version 11\n" +
                " description Bla\n" +
                " Someone with role ARole\n" +
                " Task show Object\n" +
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
        };
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(listener, process);

        assertThat(results, hasEntry("process", "Xyz"));
        assertThat(results, hasEntry("version", "11"));
    }
}