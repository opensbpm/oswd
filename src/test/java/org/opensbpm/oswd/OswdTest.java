package org.opensbpm.oswd;

import org.junit.jupiter.api.Test;
import org.opensbpm.engine.api.model.definition.ProcessDefinition;
import org.opensbpm.oswd.jxpath.JXPath;
import org.opensbpm.oswd.parser.ProcessParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.opensbpm.oswd.matchers.OswdMatchers.*;


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
