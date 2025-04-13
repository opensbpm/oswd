package org.opensbpm.oswd.convert;

import org.opensbpm.engine.api.model.builder.*;
import org.opensbpm.engine.api.model.builder.ProcessBuilder;
import org.opensbpm.engine.api.model.definition.ProcessDefinition;
import org.opensbpm.oswd.*;
import org.opensbpm.oswd.Subject.Role;
import org.opensbpm.oswd.ReceiveTask.Message;

import org.opensbpm.engine.api.model.FieldType;

import org.opensbpm.engine.api.model.builder.FunctionStateBuilder.AbstractAttributePermissionBuilder;
import org.opensbpm.engine.api.model.builder.FunctionStateBuilder.PermissionBuilder;
import org.opensbpm.engine.api.model.builder.ObjectBuilder.AttributeBuilder;
import org.opensbpm.engine.api.model.builder.ObjectBuilder.FieldBuilder;
import org.opensbpm.engine.api.model.definition.PermissionDefinition.Permission;

import java.util.*;
import java.util.function.Function;

import org.opensbpm.engine.api.model.definition.ObjectDefinition.AttributeDefinition;
import org.opensbpm.engine.api.model.definition.SubjectDefinition;
import org.opensbpm.oswd.Process;

import static org.opensbpm.engine.api.model.builder.DefinitionFactory.*;

public class ProcessConverter {

    private final Map<String, ObjectBuilder> objectCache = new HashMap<>();

    public ProcessDefinition convert(Process processType) {
        ProcessBuilder processBuilder = process(processType.getName())
                .description(processType.getDescription())
                .version(processType.getVersion());

        processType.accept(new OswdVisitor() {
            private UserSubjectBuilder subjectBuilder;
            private FunctionStateBuilder functionState;

            @Override
            public void visitProcess(Process process) {

            }

            @Override
            public void visitSubject(Subject subject) {
                subjectBuilder = userSubject(subject.getName(), subject.getRole().getName());
                processBuilder.addSubject(subjectBuilder);
            }

            @Override
            public void visitRole(Role role) {
                //noop
            }

            @Override
            public void visitShowTask(ShowTask showTask) {
                functionState = functionState(showTask.getName())
                        .withDisplayName(showTask.getName());

                subjectBuilder.addState(functionState);
            }

            @Override
            public void visitBusinessObject(BusinessObject businessObject) {
                ObjectBuilder objectBuilder = objectCache.computeIfAbsent(businessObject.getName(), s -> {
                    ObjectBuilder builder = object(businessObject.getName())
                            .withDisplayName(businessObject.getName());
                    processBuilder.addObject(builder);
                    return builder;
                });

                for (Attribute attribute : businessObject.getAttributes()) {
                    FieldBuilder fieldBuilder = field(attribute.getName(), asFieldType(((ScalarAttribute) attribute).getAttributeType()));
                    objectBuilder.addAttribute(fieldBuilder);
                }

                PermissionBuilder permissionBuilder = permission(objectBuilder)
                        .addPermissions(createAttribute(objectBuilder, businessObject.getAttributes()));
                functionState.addPermission(permissionBuilder);
            }

            @Override
            public void visitScalarAttribute(ScalarAttribute attribute) {

            }

            @Override
            public void visitToOneAttribute(ToOneAttribute attribute) {

            }

            @Override
            public void visitToManyAttribute(ToManyAttribute attribute) {

            }

            @Override
            public void visitSendTask(SendTask sendTask) {
                SendStateBuilder sendState = sendState(sendTask.getName(),
                        processBuilder.getSubject(sendTask.getReceiverSubjectName()),
                        processBuilder.getObject(sendTask.getObjectNameReference())
                );
                subjectBuilder.addState(sendState);
            }

            @Override
            public void visitReceiveTask(ReceiveTask receiveTask) {
                ReceiveStateBuilder receiveStateBuilder = receiveState(receiveTask.getName());
                subjectBuilder.addState(receiveStateBuilder);
            }

            @Override
            public void visitMessage(Message message) {

            }

            @Override
            public void visitProceedTo(String proceedTo) {

            }
        });

        for (Subject subject : processType.getSubjects()) {
            updateSubject(processBuilder, subject);
        }

        return processBuilder.build();
    }

    private void updateSubject(ProcessBuilder processBuilder, Subject subject) {
        SubjectBuilder<?, ? extends SubjectDefinition> subjectBuilder;
        subjectBuilder = processBuilder.getSubject(subject.getName());

        updateHeads(subject, subjectBuilder, processBuilder);
    }

    private static FieldType asFieldType(AttributeType attributeType) {
        switch (attributeType) {
            case BOOLEAN:
                return FieldType.BOOLEAN;
            case NUMBER:
                return FieldType.NUMBER;
            case DATE:
                return FieldType.DATE;
            case TEXT:
                return FieldType.STRING;
            default:
                throw new UnsupportedOperationException("AttributeType " + attributeType + " not supported yet");
        }
    }

    private void updateHeads(Subject subject, SubjectBuilder<?, ?> subjectBuilder, ProcessBuilder processBuilder) {
        for (Task task : subject.getTasks()) {
            if (task instanceof ShowTask) {
                FunctionStateBuilder functionStateBuilder = (FunctionStateBuilder) subjectBuilder.getState(task.getName());
                String toState = ((ShowTask) task).getProceedTo();
                StateBuilder<?, ?> toStateBuilder = subjectBuilder.getState(toState);
                functionStateBuilder.toHead(toStateBuilder);
            } else if (task instanceof ReceiveTask) {
                ReceiveStateBuilder receiveStateBuilder = (ReceiveStateBuilder) subjectBuilder.getState(task.getName());
                for (Message transitionType : ((ReceiveTask) task).getMessages()) {
                    ObjectBuilder objectBuilder = processBuilder.getObject(transitionType.getObjectNameReference());
                    StateBuilder<?, ?> toStateBuilder = subjectBuilder.getState(transitionType.getTaskNameReference());

                    receiveStateBuilder.toHead(objectBuilder, toStateBuilder);
                }
            } else if (task instanceof SendTask) {
                SendStateBuilder sendStateBuilder = (SendStateBuilder) subjectBuilder.getState(task.getName());

                Optional.ofNullable(((SendTask) task).getProceedTo())
                        .map(stateName -> subjectBuilder.getState(stateName))
                        .ifPresent(toStateBuilder -> sendStateBuilder.toHead(toStateBuilder));

            } else {
                throw new UnsupportedOperationException("StateType " + task + " not supported yet");
            }

        }
    }

    private List<AbstractAttributePermissionBuilder<?, ?>> createAttribute(HasChildAttributes<?> parentAttributeBuilder, Collection<Attribute> attributes) {
        List<AbstractAttributePermissionBuilder<?, ?>> permissions = new ArrayList<>();
        for (Attribute attribute : attributes) {
            AttributeBuilder<? extends AttributeDefinition, ?> attributeBuilder = parentAttributeBuilder.getAttribute(attribute.getName());
            AbstractAttributePermissionBuilder<?, ?> permissionBuilder = simplePermission(attributeBuilder,
                    attribute.isReadonly() ? Permission.READ : Permission.WRITE,
                    attribute.isRequired());
            permissions.add(permissionBuilder);
        }
        return permissions;
    }

}
