package org.opensbpm.oswd

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

class MainTest {

    private static PrintStream stdErr
    private static PrintStream stdOut

    @BeforeAll
    static void setup() {
        stdErr = System.err
        stdOut = System.out
    }

    @AfterAll
    static void tearDown() {
        System.err = stdErr
        System.out = stdOut
    }

    @Test
    void testMainWithoutArgs() {
        //arrange
        def errStream = new ByteArrayOutputStream()
        System.err = new PrintStream(errStream)

        def outputStream = new ByteArrayOutputStream()
        System.out = new PrintStream(outputStream)

        //act
        Main.main()

        //assert
        def result = errStream.toString()
        assertThat(result, containsString("usage: oswd"));
    }

    @Test
    void testMainWithCorrectArgs() {
        //arrange
        def errStream = new ByteArrayOutputStream()
        System.err = new PrintStream(errStream)

        def outputStream = new ByteArrayOutputStream()
        System.out = new PrintStream(outputStream)

        //act
        Main.main(new String[]{
                "-input", "sample.oswd",
                "-output", "sample.oswd"
        })

        //assert
        def result = outputStream.toString()
        assertThat(result, containsString("Converted sample.oswd to sample.oswd"));
    }
}
