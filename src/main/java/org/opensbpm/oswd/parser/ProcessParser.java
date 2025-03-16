package org.opensbpm.oswd.parser;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.opensbpm.oswd.Process;
import org.opensbpm.oswd.parser.OswdParser.DefinitionContext;

import java.io.IOException;
import java.io.Reader;

public class ProcessParser {

    public static Process parseOswd(Reader reader) throws IOException {
        OswdLexer oswdLexer = new OswdLexer(CharStreams.fromReader(reader));
        CommonTokenStream tokens = new CommonTokenStream(oswdLexer);
        OswdParser parser = new OswdParser(tokens);
        DefinitionContext definitionContext = parser.definition();

        ProcessOswdListener listener = new ProcessOswdListener();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(listener, definitionContext);
        return listener.getProcess();
    }

    private ProcessParser() {
        //avoid instantiation
    }
}
