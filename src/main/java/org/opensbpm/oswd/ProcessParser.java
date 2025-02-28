package org.opensbpm.oswd;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.opensbpm.oswd.OswdParser.ProcessContext;
import org.opensbpm.oswd.OswdParser.ProcessNameContext;
import org.opensbpm.oswd.OswdParser.SubjectContext;
import org.opensbpm.oswd.OswdParser.SubjectNameContext;
import org.opensbpm.oswd.OswdParser.RoleNameContext;
import org.opensbpm.oswd.OswdParser.VersionContext;
import org.opensbpm.oswd.OswdParser.TaskContext;
import org.opensbpm.oswd.OswdParser.TaskNameContext;

import static org.opensbpm.oswd.ContextStackFactory.processItem;
import static org.opensbpm.oswd.ContextStackFactory.subjectItem;
import static org.opensbpm.oswd.ContextStackFactory.taskItem;

class ProcessParser {

    public static Process parseProcess(String content) {
        OswdParser parser = createOswdParser(content);
        OswdParser.DefinitionContext definitionContext = parser.definition();
        MyOswdBaseListener listener = new MyOswdBaseListener();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(listener, definitionContext);
        return listener.getProcess();
    }

    private static OswdParser createOswdParser(String content) {
        OswdLexer oswdLexer = new OswdLexer(CharStreams.fromString(content));

        CommonTokenStream tokens = new CommonTokenStream(oswdLexer);
        return new OswdParser(tokens);
    }

    private static class MyOswdBaseListener extends OswdBaseListener {
        private final ContextStack contextStack;
        private Process process;

        public MyOswdBaseListener() {
            contextStack = new ContextStack();
        }

        @Override
        public void enterProcess(ProcessContext ctx) {
            contextStack.register(processItem(ctx));
        }

        @Override
        public void exitProcess(ProcessContext ctx) {
            process = contextStack.pop(processItem(ctx))
                    .build();
        }

        public void enterProcessName(ProcessNameContext ctx) {
            contextStack.peek(processItem((ProcessContext) ctx.parent))
                    .withName(ctx.IDENTIFIER().getText());
        }

        @Override
        public void enterVersion(VersionContext ctx) {
            contextStack.peek(processItem((ProcessContext) ctx.parent))
                    .withVersion(Integer.parseInt(ctx.INT().getText()));
        }

        @Override
        public void enterSubject(SubjectContext ctx) {
            contextStack.register(subjectItem(ctx));
        }

        @Override
        public void exitSubject(SubjectContext ctx) {
            Subject subjet = contextStack.pop(subjectItem(ctx))
                    .build();
            contextStack.peek(processItem((ProcessContext) ctx.parent))
                    .addSubject(subjet);
        }

        @Override
        public void enterSubjectName(SubjectNameContext ctx) {
            contextStack.peek(subjectItem((SubjectContext) ctx.parent))
                    .withName(ctx.IDENTIFIER().getText());
        }

        @Override
        public void enterRoleName(RoleNameContext ctx) {
            contextStack.peek(subjectItem((SubjectContext) ctx.parent))
                    .withRoleName(ctx.IDENTIFIER().getText());
        }

        @Override
        public void enterTask(TaskContext ctx) {
            contextStack.register(taskItem(ctx));
        }

        @Override
        public void exitTask(TaskContext ctx) {
            Task task = contextStack.peek(taskItem(ctx))
                    .build();
            contextStack.peek(subjectItem((SubjectContext) ctx.parent))
                    .addTask(task);
        }

        @Override
        public void enterTaskName(TaskNameContext ctx) {
            contextStack.peek(taskItem((TaskContext) ctx.parent))
                    .withName(ctx.IDENTIFIER().getText());
        }

        @Override
        public void exitProceed(OswdParser.ProceedContext ctx) {
            ctx.IDENTIFIER().getText();
        }

        public Process getProcess() {
            return process;
        }
    }
}
