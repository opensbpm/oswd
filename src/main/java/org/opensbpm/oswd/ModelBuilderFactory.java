package org.opensbpm.oswd;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.opensbpm.oswd.ReceiveTask.Message;

import java.util.*;

public class ModelBuilderFactory {

    public static ProcessBuilder createProcessBuilder() {
        return new ProcessBuilder();
    }

    public static SubjectBuilder createSubjectBuilder() {
        return new SubjectBuilder();
    }

    public static ShowTaskBuilder createShowTaskBuilder() {
        return new ShowTaskBuilder();
    }

    public static SendTaskBuilder createSendTaskBuilder() {
        return new SendTaskBuilder();
    }

    public static ReceiveTaskBuilder createReceiveTaskBuilder() {
        return new ReceiveTaskBuilder();
    }

    public static BusinessObjectBuilder createBusinessObjectBuilder() {
        return new BusinessObjectBuilder();
    }

    public static AttributeBuilder createAttributeBuilder() {
        return new AttributeBuilder();
    }

    public static interface ModelBuilder<T> {
        public T build();
    }

    public static abstract class AbstractBuilder<T, B extends AbstractBuilder<T, B>> implements ModelBuilder<T> {
        protected String name;

        protected abstract B self();

        public final B withName(String name) {
            this.name = Objects.requireNonNull(name, "Name must not be null");
            return self();
        }

    }

    public static class ProcessBuilder extends AbstractBuilder<Process, ProcessBuilder> {
        private int version;
        private Collection<Subject> subjects = new ArrayList<>();

        public ProcessBuilder withVersion(int version) {
            this.version = version;
            return self();
        }

        @Override
        protected ProcessBuilder self() {
            return this;
        }

        public ProcessBuilder addSubject(Subject subject) {
            this.subjects.add(subject);
            return self();
        }

        public Process build() {
            return new Process() {
                @Override
                public String getName() {
                    return name;
                }

                @Override
                public int getVersion() {
                    return version;
                }

                @Override
                public Collection<Subject> getSubjects() {
                    return new ArrayList<>(subjects);
                }
            };
        }
    }

    public static class SubjectBuilder extends AbstractBuilder<Subject, SubjectBuilder> {
        private String roleName;
        private List<Task> tasks = new ArrayList<>();

        @Override
        protected SubjectBuilder self() {
            return this;
        }

        public SubjectBuilder withRoleName(String name) {
            this.roleName = Objects.requireNonNull(name, "Role name must not be null");
            return self();
        }

        public SubjectBuilder addTask(Task task) {
            this.tasks.add(task);
            return self();
        }

        public Subject build() {
            return new Subject() {
                @Override
                public String getName() {
                    return name;
                }

                @Override
                public Role getRole() {
                    return new Role() {
                        @Override
                        public String getName() {
                            return roleName;
                        }
                    };
                }

                public Collection<Task> getTasks() {
                    return tasks;
                }
            };
        }

    }

    public static abstract class AbstractTaskBuilder<T extends Task, B extends AbstractTaskBuilder<T, B>> extends AbstractBuilder<T, B> {
    }

    public static class ShowTaskBuilder extends AbstractTaskBuilder<ShowTask, ShowTaskBuilder> {

        private BusinessObject businessObject;
        private String proceedTo;

        @Override
        protected ShowTaskBuilder self() {
            return this;
        }

        public ShowTaskBuilder withBusinessObject(BusinessObject businessObject) {
            this.businessObject = Objects.requireNonNull(businessObject, "BusinessObject name must not be null");
            return self();
        }


        public ShowTaskBuilder withProceedTo(String taskNameReference) {
            this.proceedTo = Objects.requireNonNull(taskNameReference, "taskNameReference must not be null");
            return self();
        }

        public ShowTask build() {
            return new ShowTask() {

                @Override
                public String getName() {
                    return name;
                }

                @Override
                public BusinessObject getBusinessObject() {
                    return businessObject;
                }

                @Override
                public String getProceedTo() {
                    return proceedTo;
                }
            };
        }
    }

    public static class SendTaskBuilder extends AbstractTaskBuilder<SendTask, SendTaskBuilder> {
        private String objectNameReference;
        private String receiverSubjectName;
        private String proceedTo;

        @Override
        protected SendTaskBuilder self() {
            return this;
        }

        public SendTaskBuilder withObjectNameReference(String objectNameReference) {
            this.objectNameReference = Objects.requireNonNull(objectNameReference, "objectNameReference must not be null");
            return self();
        }

        public SendTaskBuilder withReceiverSubjectName(String receiverSubjectName) {
            this.receiverSubjectName = Objects.requireNonNull(receiverSubjectName, "receiverSubjectName must not be null");
            return self();
        }

        public SendTaskBuilder withProceedTo(String taskName) {
            this.proceedTo = Objects.requireNonNull(taskName, "taskName must not be null");
            return self();
        }

        public SendTask build() {
            return new SendTask() {

                @Override
                public String getName() {
                    return name;
                }

                @Override
                public String getObjectNameReference() {
                    return objectNameReference;
                }

                @Override
                public String getReceiverSubjectName() {
                    return receiverSubjectName;
                }

                @Override
                public String getProceedTo() {
                    return proceedTo;
                }

                @Override
                public String toString() {
                    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
                            append("name", name).
                            append("objectNameReference", objectNameReference).
                            append("receiverSubjectName", receiverSubjectName).
                            append("proceedTo", proceedTo).
                            toString();
                }
            };
        }
    }

    public static class ReceiveTaskBuilder extends AbstractTaskBuilder<ReceiveTask, ReceiveTaskBuilder> {

        private Collection<Message> messages = new ArrayList<>();

        @Override
        protected ReceiveTaskBuilder self() {
            return this;
        }

        public ReceiveTaskBuilder addMessage(String objectNameReference, String taskNameReference) {
            this.messages.add(new Message() {

                @Override
                public String getObjectNameReference() {
                    return objectNameReference;
                }

                @Override
                public String getTaskNameReference() {
                    return taskNameReference;
                }

                @Override
                public String toString() {
                    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
                            append("objectNameReference", objectNameReference).
                            append("taskNameReference", taskNameReference).
                            toString();
                }

            });
            return self();
        }

        public ReceiveTask build() {
            return new ReceiveTask() {

                @Override
                public String getName() {
                    return name;
                }

                @Override
                public Collection<Message> getMessages() {
                    return Collections.unmodifiableCollection(messages);
                }

                @Override
                public String toString() {
                    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
                            append("name", name).
                            append("messages", messages).
                            toString();
                }

            };
        }
    }

    public static class BusinessObjectBuilder extends AbstractBuilder<BusinessObject, BusinessObjectBuilder> {

        private List<Attribute> attributes = new ArrayList<>();

        @Override
        protected BusinessObjectBuilder self() {
            return this;
        }


        public BusinessObjectBuilder addAttribute(Attribute attribute) {
            this.attributes.add(attribute);
            return self();
        }


        public BusinessObject build() {
            return new BusinessObject() {

                @Override
                public String getName() {
                    return name;
                }

                @Override
                public Collection<Attribute> getAttributes() {
                    return attributes;
                }
            };
        }
    }

    public static class AttributeBuilder extends AbstractBuilder<Attribute, AttributeBuilder> {

        private AttributeType attributeType;
        private boolean required;
        private boolean readonly;

        @Override
        protected AttributeBuilder self() {
            return this;
        }

        public AttributeBuilder withType(AttributeType attributeType) {
            this.attributeType = Objects.requireNonNull(attributeType, "attributeType must not be null");
            return self();
        }

        public AttributeBuilder asRequired() {
            this.required = true;
            return self();
        }

        public AttributeBuilder asReadonly() {
            this.readonly = true;
            return self();
        }

        public Attribute build() {
            return new Attribute() {

                @Override
                public String getName() {
                    return name;
                }

                @Override
                public AttributeType getAttributeType() {
                    return attributeType;
                }

                @Override
                public boolean isRequired() {
                    return required;
                }

                @Override
                public boolean isReadonly() {
                    return readonly;
                }
            };
        }
    }

    private ModelBuilderFactory() {
        //avoid instantiation
    }
}
