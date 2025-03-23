package org.opensbpm.oswd.convert;

import org.opensbpm.engine.api.model.definition.*;
import org.opensbpm.engine.api.model.definition.PermissionDefinition.AttributePermissionDefinition;
import org.opensbpm.engine.api.model.definition.StateDefinition.FunctionStateDefinition;
import org.opensbpm.engine.api.model.definition.StateDefinition.ReceiveStateDefinition;
import org.opensbpm.engine.api.model.definition.StateDefinition.SendStateDefinition;
import org.opensbpm.engine.api.model.definition.SubjectDefinition.UserSubjectDefinition;
import org.opensbpm.engine.api.model.definition.ObjectDefinition.AttributeDefinition;
import org.opensbpm.engine.api.model.definition.ObjectDefinition.AttributeDefinitionVisitor;
import org.opensbpm.engine.api.model.definition.ObjectDefinition.FieldDefinition;
import org.opensbpm.engine.api.model.definition.ObjectDefinition.ToOneDefinition;
import org.opensbpm.engine.api.model.definition.ObjectDefinition.ToManyDefinition;
import org.opensbpm.oswd.*;
import org.opensbpm.oswd.Process;
import org.opensbpm.oswd.Process.ProcessBuilder;
import org.opensbpm.oswd.Subject.SubjectBuilder;
import org.opensbpm.oswd.ShowTask.ShowTaskBuilder;
import org.opensbpm.oswd.BusinessObject.BusinessObjectBuilder;
import org.opensbpm.oswd.ScalarAttribute.ScalarAttributeBuilder;
import org.opensbpm.oswd.NestedAttribute;
import org.opensbpm.oswd.NestedAttribute.NestedAttributeBuilder;

import java.util.List;

public class ProcessDefinitionConverter {

    public Process convert(ProcessDefinition processType) {
        ProcessBuilder processBuilder = Process.builder()
                .withName(processType.getName())
                .withVersion(processType.getVersion())
                .withDescription(processType.getDescription());

        for (SubjectDefinition subject : processType.getSubjects()) {
            processBuilder.addSubject(convertSubject(subject));
        }
        return processBuilder.build();
    }

    private Subject convertSubject(SubjectDefinition subjectDefinition) {
        SubjectBuilder subjectBuilder = Subject.builder()
                .withName(subjectDefinition.getName());
        if (subjectDefinition instanceof UserSubjectDefinition) {
            String roleName = ((UserSubjectDefinition) subjectDefinition).getRoles().iterator().next();
            subjectBuilder.withRoleName(roleName);
        }
        for (StateDefinition stateDefinition : subjectDefinition.getStates()) {
            Task task = stateDefinition.accept(new StateDefinitionVisitor<Task>() {

                @Override
                public Task visitFunctionState(FunctionStateDefinition functionStateDefinition) {
                    ShowTaskBuilder showTaskBuilder = ShowTask.builder()
                            .withName(functionStateDefinition.getName());
                    if (!functionStateDefinition.getPermissions().isEmpty()) {
                        showTaskBuilder.withBusinessObject(convertBusinessObject(functionStateDefinition.getPermissions()));
                    }
                    if (!functionStateDefinition.getHeads().isEmpty()) {
                        showTaskBuilder.withProceedTo(functionStateDefinition.getHeads().iterator().next().getName());
                    }
                    return showTaskBuilder.build();
                }

                @Override
                public Task visitReceiveState(ReceiveStateDefinition receiveStateDefinition) {
                    return ReceiveTask.builder()
                            .withName(receiveStateDefinition.getName())
                            .build();
                }

                @Override
                public Task visitSendState(SendStateDefinition sendStateDefinition) {
                    return SendTask.builder()
                            .withName(sendStateDefinition.getName())
                            .build();
                }
            });
            subjectBuilder.addTask(task);
        }
        return subjectBuilder.build();
    }

    private BusinessObject convertBusinessObject(List<PermissionDefinition> permissions) {
        BusinessObjectBuilder objectBuilder = BusinessObject.builder()
                .withName(permissions.iterator().next().getObjectDefinition().getName());

        for (PermissionDefinition permission : permissions) {
            for (AttributePermissionDefinition attributeDefinition : permission.getAttributePermissions()) {
                Attribute attribute = convertAttribute(attributeDefinition.getAttribute());
                objectBuilder.addAttribute(attribute);
            }
        }
        return objectBuilder
                .build();
    }

    private static Attribute convertAttribute(final AttributeDefinition attributeDefinition) {
        return attributeDefinition.accept(new AttributeDefinitionVisitor<Attribute>() {
            @Override
            public Attribute visitField(FieldDefinition fieldDefinition) {
                ScalarAttributeBuilder attributeBuilder = ScalarAttribute.builder()
                        .withName(attributeDefinition.getName());
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
                attributeBuilder.withType(type);
                return attributeBuilder.build();
            }

            @Override
            public Attribute visitToOne(ToOneDefinition toOneDefinition) {
                return convertNested(ToOneAttribute.builder(), toOneDefinition);
            }

            @Override
            public Attribute visitToMany(ToManyDefinition toManyDefinition) {
                return convertNested(ToManyAttribute.builder(), toManyDefinition);
            }
        });
    }

    private static <T extends NestedAttribute, B extends NestedAttributeBuilder<T, B>> T convertNested(NestedAttributeBuilder<T, B> attributeBuilder, ObjectDefinition.NestedAttribute nestedDefinition) {
        attributeBuilder.withName(nestedDefinition.getName());

        BusinessObjectBuilder objectBuilder = BusinessObject.builder().withName(nestedDefinition.getName());
        for (AttributeDefinition attributeDefinition : nestedDefinition.getAttributes()) {
            Attribute attribute = convertAttribute(attributeDefinition);
            objectBuilder.addAttribute(attribute);
        }
        attributeBuilder.withBusinessObject(objectBuilder.build());
        return attributeBuilder.build();
    }

}
