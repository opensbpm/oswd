package org.opensbpm.oswd

import org.opensbpm.engine.api.model.FieldType
import org.opensbpm.engine.api.model.builder.*
import org.opensbpm.engine.api.model.builder.FunctionStateBuilder.AbstractAttributePermissionBuilder
import org.opensbpm.engine.api.model.builder.FunctionStateBuilder.PermissionBuilder
import org.opensbpm.engine.api.model.builder.ObjectBuilder.AttributeBuilder
import org.opensbpm.engine.api.model.builder.ProcessBuilder
import org.opensbpm.engine.api.model.builder.ObjectBuilder.FieldBuilder
import org.opensbpm.engine.api.model.definition.ObjectDefinition.AttributeDefinition
import org.opensbpm.engine.api.model.definition.PermissionDefinition.Permission
import org.opensbpm.engine.api.model.definition.ProcessDefinition
import org.opensbpm.engine.api.model.definition.SubjectDefinition
import org.opensbpm.oswd.model.Attribute
import org.opensbpm.oswd.model.AttributeType
import org.opensbpm.oswd.model.Message
import org.opensbpm.oswd.model.Object
import org.opensbpm.oswd.model.OswdVisitor
import org.opensbpm.oswd.model.ProceedTo
import org.opensbpm.oswd.model.Process
import org.opensbpm.oswd.model.Receive
import org.opensbpm.oswd.model.Send
import org.opensbpm.oswd.model.Subject
import org.opensbpm.oswd.model.Task
import org.opensbpm.oswd.model.Taskable

import static org.opensbpm.oswd.model.AttributeType.*


import static java.util.Collections.emptyList
import static org.opensbpm.engine.api.model.builder.DefinitionFactory.*

public class ProcessConverter {

    private final Map<String, ObjectBuilder> objectCache = new HashMap<>()

    public ProcessDefinition convert(Process processType) {
        ProcessBuilder processBuilder = process(processType.getName())
                .description(processType.getDescription())
                .version(processType.getVersion())

        processType.accept(new OswdVisitor()  {
            private UserSubjectBuilder subjectBuilder
            private FunctionStateBuilder functionState

            @Override
            public void visitProcess(Process process) {
                //noop
            }

            @Override
            public void visitSubject(Subject subject) {
                subjectBuilder = getSubject(subject.getName())
                subjectBuilder.addRole(subject.getRole())
                processBuilder.addSubject(subjectBuilder)
            }

            @Override
            public void visitTask(Task task) {
                functionState = functionState(task.getName())
                        .withDisplayName(task.getName())

                subjectBuilder.addState(functionState)
            }

            @Override
            public void visitObject(Object bobject) {
                ObjectBuilder objectBuilder = objectCache.computeIfAbsent(bobject.getName(), s -> {
                    ObjectBuilder builder = object(bobject.getName())
                            .withDisplayName(bobject.getName())
                    processBuilder.addObject(builder)
                    return builder
                })

                for (Attribute attribute : bobject.getAttributes()) {
                    FieldBuilder fieldBuilder = field(attribute.getName(), asFieldType((attribute.getType())))
                    objectBuilder.addAttribute(fieldBuilder)
                }

                PermissionBuilder permissionBuilder = permission(objectBuilder)
                        .addPermissions(createAttribute(objectBuilder, bobject.getAttributes()))
                functionState.addPermission(permissionBuilder)
            }

            @Override
            public void visitAttribute(Attribute attribute) {
                //noop
            }

            @Override
            public void visitProceedTo(ProceedTo proceedTo) {

            }

            @Override
            public void visitSend(Send send) {
                SendStateBuilder sendState = sendState(send.getName(),
                        getSubject(send.getReceiver()),
                        processBuilder.getObject(send.getMessage())
                )
                subjectBuilder.addState(sendState)
            }

            private UserSubjectBuilder getSubject(String name) {
                SubjectBuilder<?, ? extends SubjectDefinition> subjectBuilder = processBuilder.getSubject(name)
                if (subjectBuilder == null) {
                    subjectBuilder = userSubject(name, emptyList())
                    processBuilder.addSubject(subjectBuilder)
                }
                return (UserSubjectBuilder) subjectBuilder
            }

            @Override
            public void visitReceive(Receive receive) {
                ReceiveStateBuilder receiveStateBuilder = receiveState(receive.getName())

                subjectBuilder.addState(receiveStateBuilder)
            }

        })

        for (Subject subject : processType.getSubjects()) {
            updateSubject(processBuilder, subject)
        }

        return processBuilder.build()
    }

    private void updateSubject(ProcessBuilder processBuilder, Subject subject) {
        SubjectBuilder<?, ? extends SubjectDefinition> subjectBuilder
        subjectBuilder = processBuilder.getSubject(subject.getName())

        updateHeads(subject, subjectBuilder, processBuilder)
    }

    private static FieldType asFieldType(AttributeType attributeType) {
        switch (attributeType) {
            case BOOLEAN:
                return FieldType.BOOLEAN
            case NUMBER:
                return FieldType.NUMBER
            case DATE:
                return FieldType.DATE
            case TEXT:
                return FieldType.STRING
            default:
                throw new UnsupportedOperationException("AttributeType " + attributeType + " not supported yet")
        }
    }

    private void updateHeads(Subject subject, SubjectBuilder<?, ?> subjectBuilder, ProcessBuilder processBuilder) {
        for (Taskable task : subject.getTasks()) {
            if (task instanceof Task) {
                FunctionStateBuilder functionStateBuilder = (FunctionStateBuilder) subjectBuilder.getState(task.getName())
                if (((Task) task).getProceedTos().isEmpty()) {
                    functionStateBuilder.asEnd()
                } else {
                    for (ProceedTo proceedTo : ((Task) task).getProceedTos()) {
                        StateBuilder<?, ?> toStateBuilder = subjectBuilder.getState(proceedTo.getName())
                        functionStateBuilder.toHead(toStateBuilder)
                    }
                }
            } else if (task instanceof Receive) {
                ReceiveStateBuilder receiveStateBuilder = (ReceiveStateBuilder) subjectBuilder.getState(task.getName())
                for (Message transitionType : ((Receive) task).getMessages()) {
                    ObjectBuilder objectBuilder = processBuilder.getObject(transitionType.getObject())
                    StateBuilder<?, ?> toStateBuilder = subjectBuilder.getState(transitionType.getProceedTo())

                    receiveStateBuilder.toHead(objectBuilder, toStateBuilder)
                }
            } else if (task instanceof Send) {
                SendStateBuilder sendStateBuilder = (SendStateBuilder) subjectBuilder.getState(task.getName())

                Optional.ofNullable(((Send) task).getProceedTo())
                        .map(stateName -> subjectBuilder.getState(stateName))
                        .ifPresent(toStateBuilder -> sendStateBuilder.toHead(toStateBuilder))

            } else {
                throw new UnsupportedOperationException("StateType " + task + " not supported yet")
            }

        }
    }

    private List<AbstractAttributePermissionBuilder<?, ?>> createAttribute(HasChildAttributes<?> parentAttributeBuilder, Collection<Attribute> attributes) {
        List<AbstractAttributePermissionBuilder<?, ?>> permissions = new ArrayList<>()
        for (Attribute attribute : attributes) {
            AttributeBuilder<? extends AttributeDefinition, ?> attributeBuilder = parentAttributeBuilder.getAttribute(attribute.getName())
            AbstractAttributePermissionBuilder<?, ?> permissionBuilder = simplePermission(attributeBuilder,
                    attribute.isReadonly() ? Permission.READ : Permission.WRITE,
                    attribute.isRequired())
            permissions.add(permissionBuilder)
        }
        return permissions
    }

}
