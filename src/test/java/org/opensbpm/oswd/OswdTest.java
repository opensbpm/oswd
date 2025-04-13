package org.opensbpm.oswd;

import groovy.lang.GroovyShell;
import org.junit.jupiter.api.Test;
import org.opensbpm.engine.api.model.definition.ProcessDefinition;
import org.opensbpm.oswd.parser.ProcessParser;

import java.io.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class OswdTest {

    @Test
    public void testToOswd() throws Exception {
        //arrange
        InputStream is = getClass().getResourceAsStream("/sample.oswd");
        InputStreamReader reader = new InputStreamReader(is);
        Process process = ProcessParser.parseOswd(reader);
        reader.close();

        //act
        String oswdContent = Oswd.toOswd(process);

        //assert
        is = getClass().getResourceAsStream("/sample.oswd");
        String result = new BufferedReader(new InputStreamReader(is))
                .lines().parallel().collect(Collectors.joining("\n"));
        assertThat(oswdContent, is(result));
    }
}
