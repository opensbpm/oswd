package org.opensbpm.oswd;

import org.opensbpm.engine.api.model.definition.ProcessDefinition;
import org.opensbpm.oswd.Subject.Role;
import org.opensbpm.oswd.ReceiveTask.Message;
import org.opensbpm.oswd.convert.ProcessConverter;
import org.opensbpm.oswd.parser.ProcessParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class Oswd {

    public static ProcessDefinition parseOswd(File file) throws IOException {
        Process process;
        try (FileReader reader = new FileReader(file)) {
            process = Oswd.parseOswd(reader);
        }
        return new ProcessConverter().convert(process);
    }

    public static Process parseOswd(Reader reader) throws IOException {
        return ProcessParser.parseOswd(reader);
    }

    public static String toOswd(Process process) throws IOException {
        StringBuilder sb = new StringBuilder();

        process.accept(new OswdVisitor() {
            @Override
            public void visitProcess(Process process) {
                sb.append(String.format("" +
                                "process %s\n" +
                                " version %s\n" +
                                " description %s\n",
                        process.getName(),
                        process.getVersion(),
                        process.getDescription()
                ));
            }

            @Override
            public void visitSubject(Subject subject) {
                sb.append(String.format(" %s ",
                        subject.getName()
                ));
            }

            @Override
            public void visitRole(Role role) {
                sb.append(String.format("with role %s\n",
                        role.getName()
                ));

            }

            @Override
            public void visitShowTask(ShowTask showTask) {
                sb.append(String.format("  %s ",
                        showTask.getName()
                ));
            }

            @Override
            public void visitBusinessObject(BusinessObject businessObject) {
                sb.append(String.format("show %s\n",
                        businessObject.getName())
                );
            }

            @Override
            public void visitScalarAttribute(ScalarAttribute attribute) {
                sb.append(String.format("   with %s as %s%s%s\n",
                        attribute.getName(),
                        attribute.getAttributeType().getToken(),
                        attribute.isRequired() ? " required" : "",
                        attribute.isReadonly() ? " readonly" : ""
                ));
            }

            @Override
            public void visitToOneAttribute(ToOneAttribute attribute) {

            }

            @Override
            public void visitToManyAttribute(ToManyAttribute attribute) {

            }

            @Override
            public void visitProceedTo(String proceedTo) {
                sb.append(String.format("   proceed to %s\n",
                        proceedTo)
                );
            }

            @Override
            public void visitSendTask(SendTask sendTask) {
                sb.append(String.format("  %s send %s to %s\n",
                        sendTask.getName(),
                        sendTask.getObjectNameReference(),
                        sendTask.getReceiverSubjectName()
                ));
            }

            @Override
            public void visitReceiveTask(ReceiveTask receiveTask) {
                sb.append(String.format("  %s receive",
                        receiveTask.getName()
                ));

            }

            @Override
            public void visitMessage(Message message) {
                sb.append(String.format(" %s proceed to %s\n",
                        message.getObjectNameReference(),
                        message.getTaskNameReference()
                ));

            }
        });
        return sb.toString();
    }

    private Oswd() {
        //avoid instantiation
    }
}
