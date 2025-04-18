package org.opensbpm.oswd

import org.junit.jupiter.api.Test
import org.opensbpm.engine.examples.ExampleModels

import java.nio.file.Files
import java.nio.file.Path

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.containsString

class OswdConverterTest {

    @Test
    void testConvert() {
        // Arrange
        def tempDir = Files.createTempDirectory("oswd")
        Path sourceXml = tempDir.resolve("source.xml");
        Files.copy(ExampleModels.bookPage103, sourceXml)

        Path targetXml = Files.createFile(tempDir.resolve("target.xml"));

        def converter = new OswdConverter(sourceXml.toFile(),targetXml.toFile())

        // Act
        converter.convert()

        // Assert
        def result = targetXml.toFile().text
        assertThat(result, containsString("Seite 103"));
    }
}
