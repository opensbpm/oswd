import org.junit.jupiter.api.Test
import org.opensbpm.engine.api.model.definition.ProcessDefinition
import org.opensbpm.oswd.ProcessBuilder
import org.opensbpm.oswd.ProcessConverter

//import static org.opensbpm.oswd.ProcessBuilder.process
import static org.opensbpm.oswd.AttributeType.*

class OswdTest {

    @Test
    public void testParseOswd() throws Exception {
        //arrange
        InputStream is = getClass().getResourceAsStream("/sample.groovy");
        InputStreamReader reader = new InputStreamReader(is);

        //act
        GroovyShell shell = new GroovyShell();
        org.opensbpm.oswd.model.Process process = (org.opensbpm.oswd.model.Process) shell.parse(reader).run();
        new NodePrinter().print(process)
        System.out.println(process);
        ProcessDefinition processDefinition = new ProcessConverter().convert(process);

        //assert
//        is = getClass().getResourceAsStream("/sample.oswd");
//        String result = new BufferedReader(new InputStreamReader(is))
//                .lines().parallel().collect(Collectors.joining("\n"));
//        assertThat(oswdContent, is(result));
    }



}
