package org.opensbpm.oswd.parser;

import org.antlr.v4.runtime.tree.ParseTree;
import org.opensbpm.oswd.*;
import org.opensbpm.oswd.Process;
import org.opensbpm.oswd.ScalarAttribute.ScalarAttributeBuilder;
import org.opensbpm.oswd.context.ContextStack;
import org.opensbpm.oswd.parser.OswdParser.*;

import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static org.opensbpm.oswd.parser.ContextStackFactory.*;

class ProcessOswdListener extends OswdBaseListener {
    private final ContextStack contextStack;
    private Process process;

    ProcessOswdListener() {
        contextStack = new ContextStack();
    }

    @Override
    public void enterProcess(ProcessContext ctx) {
        contextStack.push(processItem(ctx));
    }

    @Override
    public void exitProcess(ProcessContext ctx) {
        process = contextStack.pop(processItem(ctx))
                .build();
    }

    @Override
    public void enterProcessName(ProcessNameContext ctx) {
        contextStack.peek(processItem((ProcessContext) ctx.parent))
                .withName(ctx.NAME().getText());
    }

    @Override
    public void enterVersion(VersionContext ctx) {
        contextStack.peek(processItem((ProcessContext) ctx.parent))
                .withVersion(Integer.parseInt(ctx.INT().getText()));
    }

    @Override
    public void enterDescriptionDef(DescriptionDefContext ctx) {
        contextStack.peek(processItem((ProcessContext) ctx.parent))
                .withDescription(extractName(ctx.description().name()));
    }

    @Override
    public void enterSubject(SubjectContext ctx) {
        contextStack.push(subjectItem(ctx));
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
                .withName(extractName(ctx.name()));
    }

    @Override
    public void enterRoleName(RoleNameContext ctx) {
        contextStack.peek(subjectItem((SubjectContext) ctx.parent))
                .withRoleName(extractName(ctx.name()));
    }

    @Override
    public void enterTask(TaskContext ctx) {
        ofNullable(ctx.show())
                .ifPresent(show -> contextStack.push(showItem(show)));

        ofNullable(ctx.receive())
                .ifPresent(receive -> contextStack.push(receiveItem(receive)));

        ofNullable(ctx.send())
                .ifPresent(send -> contextStack.push(sendItem(send)));
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
                .withName(extractName(ctx.taskName().name()))
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
                .withProceedTo(extractName(ctx.proceed().taskNameReference().name()));
    }

    @Override
    public void enterObject(ObjectContext ctx) {
        contextStack.push(objectItem(ctx));
    }

    @Override
    public void enterObjectName(ObjectNameContext ctx) {
        contextStack.peek(objectItem((ObjectContext) ctx.parent))
                .withName(extractName(ctx.name()));
    }

    @Override
    public void enterAttribute(AttributeContext ctx) {
        ScalarAttributeBuilder attributeBuilder = contextStack.push(attributeItem(ctx))
                .withName(extractName(ctx.attributeName().name()));

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
    public void enterSend(SendContext ctx) {
        contextStack.peek(sendItem(ctx))
                .withObjectNameReference(extractName(ctx.objectNameReference().name()))
                .withProceedTo(extractName(ctx.proceed().taskNameReference().name()))
        ;
    }

    @Override
    public void enterSubjectNameReference(SubjectNameReferenceContext ctx) {
        contextStack.peek(sendItem((SendContext) ctx.parent))
                .withReceiverSubjectName(extractName(ctx.name()));
    }

    @Override
    public void enterMessage(MessageContext ctx) {
        contextStack.peek(receiveItem((ReceiveContext) ctx.parent))
                .addMessage(
                        extractName(ctx.objectNameReference().name()),
                        extractName(ctx.proceed().taskNameReference().name())
                );
    }


    public Process getProcess() {
        return process;
    }

    private static String extractName(NameContext nameContext) {
        return Optional.ofNullable(nameContext.NAME())
                .map(ParseTree::getText)
                .orElseGet(() -> nameContext.text().getText());
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
