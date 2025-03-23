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
//                .description(processType.getDescription())
                .version(processType.getVersion());
//        if (ProcessModelState.INACTIVE == processType.getState()) {
//            processBuilder.asIncative();
//        }

//        Map<ObjectBuilder, ObjectType> objectsCache = processType.getObject().stream()
//                .map(objectType -> {
//                    ObjectBuilder objectBuilder = object(objectType.getName());
//                    objectBuilder.withDisplayName(objectType.getDisplayName());
//                    processBuilder.addObject(objectBuilder);
//
//                    return Pair.of(objectBuilder, objectType);
//                })
//                .collect(PairUtils.toMap());
//
//        for (Map.Entry<ObjectBuilder, ObjectType> entry : objectsCache.entrySet()) {
//            for (ObjectBuilder.AttributeBuilder<?, ?> attributeBuilder : createAttributes(processBuilder, entry.getValue().getFieldOrToOneOrToMany())) {
//                entry.getKey().addAttribute(attributeBuilder);
//            }
//        }

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
                functionState = functionState(showTask.getName());

//            Optional.ofNullable(showTask.getProvider())
//                    .ifPresent(provider -> functionState.withProvider(provider));
//            if (showTask.getParameters() != null) {
//                for (Element element : showTask.getParameters().getAny()) {
//                    functionState.addParameter(element.getLocalName(), element.getFirstChild().getNodeValue());
//                }
//            }

                subjectBuilder.addState(functionState);
            }

            @Override
            public void visitBusinessObject(BusinessObject businessObject) {
                ObjectBuilder objectBuilder = objectCache.computeIfAbsent(businessObject.getName(), s -> {
                    ObjectBuilder builder = object(businessObject.getName());
                    //objectBuilder.withDisplayName(businessObject.getDisplayName());
                    processBuilder.addObject(builder);
                    return builder;
                });

                for (Attribute attribute : businessObject.getAttributes()) {
                    FieldBuilder fieldBuilder = field(attribute.getName(), asFieldType(((ScalarAttribute)attribute).getAttributeType()));
//                Optional.ofNullable(field.isIndexed())
//                        .ifPresent(indexed -> fieldBuilder.withIndexed(indexed));
//                Optional.ofNullable(field.getAutocomplete())
//                        .ifPresent(autocomplete
//                                -> fieldBuilder.withAutocompleteObject(processBuilder.getObject(autocomplete)));
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
//            if (((SendTask) stateType).isAsync() != null && ((SendTask) stateType).isAsync()) {
//                sendState.asAsync();
//            }
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

//    private List<AttributeBuilder<?, ?>> createAttributes(ProcessBuilder processBuilder, List<Object> attributeTypes) {
//        List<AttributeBuilder<?, ?>> attributeBuilders = new ArrayList<>();
//        for (Object attributeType : attributeTypes) {
//            AttributeBuilder<?, ?> attributeBuilder;
//            if (attributeType instanceof Field) {
//                Field field = (Field) attributeType;
//                FieldBuilder fieldBuilder = field(field.getValue(), FieldType.valueOf(field.getType().value()));
//                Optional.ofNullable(field.isIndexed())
//                        .ifPresent(indexed -> fieldBuilder.withIndexed(indexed));
//                Optional.ofNullable(field.getAutocomplete())
//                        .ifPresent(autocomplete
//                                -> fieldBuilder.withAutocompleteObject(processBuilder.getObject(autocomplete)));
//                attributeBuilder = fieldBuilder;
//            } else if (attributeType instanceof ToOneType) {
//                ToOneType toOneType = (ToOneType) attributeType;
//                ToOneBuilder toOneBuilder = toOne(toOneType.getName());
//                for (AttributeBuilder<?, ?> childBuilder : createAttributes(processBuilder, toOneType.getFieldOrToOneOrToMany())) {
//                    toOneBuilder.addAttribute(childBuilder);
//                }
//                attributeBuilder = toOneBuilder;
//            } else if (attributeType instanceof ToManyType) {
//                ToManyType toManyType = (ToManyType) attributeType;
//                ToManyBuilder toManyBuilder = toMany(toManyType.getName());
//                for (AttributeBuilder<?, ?> childBuilder : createAttributes(processBuilder, toManyType.getFieldOrToOneOrToMany())) {
//                    toManyBuilder.addAttribute(childBuilder);
//                }
//                attributeBuilder = toManyBuilder;
//            } else {
//                throw new UnsupportedOperationException("AttributeType " + attributeType + " not supported yet");
//            }
//            attributeBuilders.add(attributeBuilder);
//        }
//        return attributeBuilders;
//    }

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

//                Optional.ofNullable(attributeType.getDefaultValue())
//                        .ifPresent(permissionBuilder::addDefaultValue);


            //attributeType.
            permissions.add(permissionBuilder);
        }
        return permissions;
    }

}
