package org.opensbpm.oswd.parser;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.opensbpm.oswd.*;
import org.opensbpm.oswd.Process;
import org.opensbpm.oswd.parser.OswdParser.AttributeContext;
import org.opensbpm.oswd.parser.OswdParser.AttributeNameContext;
import org.opensbpm.oswd.parser.OswdParser.AttributeTypeContext;
import org.opensbpm.oswd.parser.OswdParser.DefinitionContext;
import org.opensbpm.oswd.parser.OswdParser.ProcessContext;
import org.opensbpm.oswd.parser.OswdParser.ProcessNameContext;
import org.opensbpm.oswd.parser.OswdParser.ProceedContext;
import org.opensbpm.oswd.parser.OswdParser.SubjectContext;
import org.opensbpm.oswd.parser.OswdParser.SubjectNameContext;
import org.opensbpm.oswd.parser.OswdParser.RoleNameContext;
import org.opensbpm.oswd.parser.OswdParser.VersionContext;
import org.opensbpm.oswd.parser.OswdParser.ObjectContext;
import org.opensbpm.oswd.parser.OswdParser.ObjectNameContext;
import org.opensbpm.oswd.parser.OswdParser.ObjectNameReferenceContext;
import org.opensbpm.oswd.parser.OswdParser.TaskContext;
import org.opensbpm.oswd.parser.OswdParser.ShowContext;
import org.opensbpm.oswd.parser.OswdParser.SendContext;
import org.opensbpm.oswd.ModelBuilderFactory.AttributeBuilder;

import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static org.opensbpm.oswd.parser.ContextStackFactory.processItem;
import static org.opensbpm.oswd.parser.ContextStackFactory.subjectItem;
import static org.opensbpm.oswd.parser.ContextStackFactory.showItem;
import static org.opensbpm.oswd.parser.ContextStackFactory.sendItem;
import static org.opensbpm.oswd.parser.ContextStackFactory.receiveItem;
import static org.opensbpm.oswd.parser.ContextStackFactory.objectItem;
import static org.opensbpm.oswd.parser.ContextStackFactory.attributeItem;

public class ProcessParser {

    public static org.opensbpm.oswd.Process parseProcess(String content) {
        OswdParser parser = createOswdParser(content);
        DefinitionContext definitionContext = parser.definition();
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
        private org.opensbpm.oswd.Process process;

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
            ofNullable(ctx.show())
                    .ifPresent(show -> contextStack.register(showItem(show)));

            ofNullable(ctx.receive())
                    .ifPresent(receive -> contextStack.register(receiveItem(receive)));

            ofNullable(ctx.send())
                    .ifPresent(send -> contextStack.register(sendItem(send)));
        }

        @Override
        public void exitTask(TaskContext ctx) {
            Task task = toExactlyOne(
                    ofNullable(ctx.show())
                            .map(show -> contextStack.pop(showItem(show))),
                    ofNullable(ctx.receive())
                            .map(receive -> contextStack.pop(receiveItem(receive))),
                    ofNullable(ctx.send())
                            .map(send -> contextStack.pop(sendItem(send)))
            )
                    .withName(ctx.taskName().IDENTIFIER().getText())
                    .build();

            contextStack.peek(subjectItem((SubjectContext) ctx.parent))
                    .addTask(task);
        }

        @Override
        public void exitShow(ShowContext ctx) {
            BusinessObject businessObject = contextStack.peek(objectItem(ctx.object()))
                    .build();

            contextStack.peek(showItem(ctx))
                    .withBusinessObject(businessObject)
                    .withProceedTo(ctx.proceed().taskNameReference().IDENTIFIER().getText());
        }

        @Override
        public void enterObject(ObjectContext ctx) {
            contextStack.register(objectItem(ctx));
        }

        @Override
        public void enterObjectName(ObjectNameContext ctx) {
            contextStack.peek(objectItem((ObjectContext) ctx.parent))
                    .withName(ctx.IDENTIFIER().getText());
        }

        @Override
        public void enterAttribute(AttributeContext ctx) {
            AttributeBuilder attributeBuilder = contextStack.register(attributeItem(ctx))
                    .withName(ctx.attributeName().IDENTIFIER().getText());

            Optional.ofNullable(ctx.required())
                    .ifPresent(atxC -> attributeBuilder.asRequired());

            Optional.ofNullable(ctx.readonly())
                    .ifPresent(atxC -> attributeBuilder.asReadonly());
        }

        @Override
        public void exitAttribute(AttributeContext ctx) {
            Attribute attribute = contextStack.pop(attributeItem(ctx))
                    .build();

            contextStack.peek(objectItem((ObjectContext) ctx.parent))
                    .addAttribute(attribute);
        }

        @Override
        public void enterAttributeType(AttributeTypeContext ctx) {
            contextStack.peek(attributeItem((AttributeContext) ctx.parent))
                    .withType(AttributeType.tokenOf(ctx.getText().trim()));

        }

        @Override
        public void enterObjectNameReference(ObjectNameReferenceContext ctx) {
                    contextStack.peek(sendItem((SendContext) ctx.parent))
                            .withObjectNameReference(ctx.getText().trim());
        }

        public Process getProcess() {
            return process;
        }

        @SafeVarargs
        private static <T> T toExactlyOne(Optional<T>... optionals) {
            return Stream.of(optionals)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .reduce((a, b) -> {
                        throw new IllegalStateException("Multiple items " + a + ", " + b);
                    })
                    .orElseThrow(() -> new IllegalStateException("No item"));
        }
    }
}
