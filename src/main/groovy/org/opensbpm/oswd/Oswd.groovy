package org.opensbpm.oswd

import org.opensbpm.engine.api.model.definition.ProcessDefinition
import org.opensbpm.oswd.convert.ProcessConverter
import org.opensbpm.oswd.convert.ProcessDefinitionConverter
import org.opensbpm.oswd.model.Process

class Oswd {

    static ProcessDefinition readOswd(Reader reader) {
        Process process = readProcess(reader)
        //new NodePrinter().print(process)
        return new ProcessConverter().convert(process)
    }

    static Process readProcess(Reader reader) {
        return (Process) new GroovyShell().parse(reader).run()
    }

    static void writeProcess(ProcessDefinition processDefinition, FileWriter writer) {
        Process process = new ProcessDefinitionConverter().convert(processDefinition)
        Freemarker.write(process, writer)
    }


}
