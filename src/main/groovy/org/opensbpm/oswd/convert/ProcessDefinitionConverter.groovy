package org.opensbpm.oswd.convert;

import org.opensbpm.engine.api.model.definition.*;
import org.opensbpm.engine.api.model.definition.PermissionDefinition.AttributePermissionDefinition;
import org.opensbpm.engine.api.model.definition.StateDefinition.FunctionStateDefinition;
import org.opensbpm.engine.api.model.definition.StateDefinition.ReceiveStateDefinition;
import org.opensbpm.engine.api.model.definition.StateDefinition.SendStateDefinition
import org.opensbpm.engine.api.model.definition.SubjectDefinition.UserSubjectDefinition;
import org.opensbpm.engine.api.model.definition.ObjectDefinition.AttributeDefinition;
import org.opensbpm.engine.api.model.definition.ObjectDefinition.AttributeDefinitionVisitor;
import org.opensbpm.engine.api.model.definition.ObjectDefinition.FieldDefinition;
import org.opensbpm.engine.api.model.definition.ObjectDefinition.ToOneDefinition;
import org.opensbpm.engine.api.model.definition.ObjectDefinition.ToManyDefinition

import java.util.stream.Collectors;

import static org.opensbpm.engine.api.model.FieldType.*;
import org.opensbpm.oswd.model.*;

class ProcessDefinitionConverter {

    Process convert(ProcessDefinition processType) {
        Process process = new Process()
        process.with {
            name = processType.getName()
            version = processType.getVersion()
            description = processType.getDescription()
        }

        for (SubjectDefinition subject : processType.getSubjects()) {
            process.subjects << convertSubject(subject);
        }
        return process;
    }

    private Subject convertSubject(SubjectDefinition subjectDefinition) {
        Subject subject = new Subject()
        subject.with {
            name = subjectDefinition.getName()
        }
        if (subjectDefinition instanceof UserSubjectDefinition) {
            String roleName = ((UserSubjectDefinition) subjectDefinition).getRoles().iterator().next();
            subject.role = roleName;
        }
        for (StateDefinition stateDefinition : subjectDefinition.getStates()) {
            Taskable task = stateDefinition.accept(new StateDefinitionVisitor<Taskable>() {

                @Override
                Taskable visitFunctionState(FunctionStateDefinition functionStateDefinition) {
                    Task task = new Task();
                    task.with {
                        name =  functionStateDefinition.getName()
                    }
                    if (!functionStateDefinition.getPermissions().isEmpty()) {
                        task.object = convertBusinessObject(functionStateDefinition.getPermissions());
                    }
                    if (!functionStateDefinition.getHeads().isEmpty()) {
                        task.proceedTos = functionStateDefinition.getHeads().stream()
                                .map(head -> new ProceedTo(head.getName()))
                                .collect(Collectors.toList());
                    }
                    return task
                }

                @Override
                Taskable visitReceiveState(ReceiveStateDefinition receiveStateDefinition) {
                    Receive receive = new Receive()
                    receive.with {
                        name = receiveStateDefinition.getName()
                        messages = receiveStateDefinition.transitions.stream()
                                .map(transition -> convertMessage(transition))
                                .collect(Collectors.toList());
                    }
                    return receive
                }

                private Message convertMessage(transition) {
                    def message = new Message()
                    message.with {
                        object = transition.getObjectDefinition().getName()
                        proceedTo = transition.getHead().getName()
                    }
                    return message
                }

                @Override
                Taskable visitSendState(SendStateDefinition sendStateDefinition) {
                    Send send = new Send()
                    send.with {
                        name = sendStateDefinition.getName()
                        message = sendStateDefinition.getObjectModel().getName()
                        receiver = sendStateDefinition.getReceiver().getName()
                    }
                    if (sendStateDefinition.getHead() != null) {
                        send.proceedTo = sendStateDefinition.getHead().getName();
                    }
                    return send
                }
            });
            subject.tasks << task;
        }
        return subject
    }

    private Object convertBusinessObject(List<PermissionDefinition> permissions) {
        Object object = new Object()
        object.with {
            name = permissions.iterator().next().getObjectDefinition().getName()
        }

        for (PermissionDefinition permission : permissions) {
            for (AttributePermissionDefinition attributeDefinition : permission.getAttributePermissions()) {
                Attribute attribute = convertAttribute(attributeDefinition.getAttribute());
                attribute.with {
                    required = attributeDefinition.isMandatory()
                    readonly = PermissionDefinition.Permission.READ == attributeDefinition.getPermission()
                }
                object.attributes << attribute;
            }
        }
        return object
    }

    private static Attribute convertAttribute(final AttributeDefinition attributeDefinition) {
        return attributeDefinition.accept(new AttributeDefinitionVisitor<Attribute>() {
            @Override
            Attribute visitField(FieldDefinition fieldDefinition) {
                Attribute attribute = new Attribute()
                        attribute.with {
                    name = fieldDefinition.getName()
                    type = determineType(fieldDefinition)
                    //TODO defaultValue = fieldDefinition.getDefaultValue()
                }
                return attribute
            }

            private AttributeType determineType(FieldDefinition fieldDefinition) {
                AttributeType type;
                switch (fieldDefinition.getFieldType()) {
                    case STRING:
                        type = AttributeType.TEXT;
                        break;
                    case NUMBER:
                        type = AttributeType.NUMBER;
                        break;
                    case DECIMAL:
                        type = AttributeType.NUMBER;
                        break;
                    case DATE:
                        type = AttributeType.DATE;
                        break;
                    case TIME:
                        type = AttributeType.TIME;
                        break;
                    case BOOLEAN:
                        type = AttributeType.BOOLEAN;
                        break;
                    case BINARY:
                        type = AttributeType.BINARY;
                        break;
                    default:
                        throw new UnsupportedOperationException("Unsupported field type: " + fieldDefinition.getFieldType());
                }
                return type
            }

            @Override
            Attribute visitToOne(ToOneDefinition toOneDefinition) {
                //return convertNested(ToOneAttribute.builder(), toOneDefinition);
            }

            @Override
            Attribute visitToMany(ToManyDefinition toManyDefinition) {
                //return convertNested(ToManyAttribute.builder(), toManyDefinition);
            }
        });
    }

//    private static <T extends NestedAttribute, B extends NestedAttributeBuilder<T, B>> T convertNested(NestedAttributeBuilder<T, B> attributeBuilder, ObjectDefinition.NestedAttribute nestedDefinition) {
//        attributeBuilder.withName(nestedDefinition.getName());
//
//        BusinessObjectBuilder objectBuilder = BusinessObject.builder().withName(nestedDefinition.getName());
//        for (AttributeDefinition attributeDefinition : nestedDefinition.getAttributes()) {
//            Attribute attribute = convertAttribute(attributeDefinition);
//            objectBuilder.addAttribute(attribute);
//        }
//        attributeBuilder.withBusinessObject(objectBuilder.build());
//        return attributeBuilder.build();
//    }

}
