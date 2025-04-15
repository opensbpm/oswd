package org.opensbpm.oswd

import org.opensbpm.engine.api.model.definition.ProcessDefinition
import org.opensbpm.oswd.model.Process

class Oswd {

    public static ProcessDefinition readOswd(Reader reader) {
        Process process = readProcess(reader)
        //new NodePrinter().print(process)
        return new ProcessConverter().convert(process)
    }

    static Process readProcess(Reader reader) {
        return (Process) new GroovyShell().parse(reader).run()
    }

}
