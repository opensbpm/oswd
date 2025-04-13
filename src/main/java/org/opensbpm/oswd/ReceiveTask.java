package org.opensbpm.oswd;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.Collection;

import static java.util.Collections.unmodifiableCollection;

public final class ReceiveTask extends AbstractNamed implements Task {

    public static ReceiveTaskBuilder builder() {
        return new ReceiveTaskBuilder();
    }

    private final Collection<Message> messages;

    public ReceiveTask() {
        messages = new ArrayList<>();
    }

    public ReceiveTask(String name, Collection<Message> messages) {
        super(name);
        if(messages.isEmpty()){
            throw new IllegalArgumentException(messages + " are empty");
        }
        this.messages = new ArrayList<>(messages);
    }

    public Collection<Message> getMessages() {
        return unmodifiableCollection(messages);
    }

    public void accept(OswdVisitor visitor) {
        visitor.visitReceiveTask(this);

        getMessages().forEach(message -> message.accept(visitor));
    }

    private ReceiveTask copy() {
        return new ReceiveTask(getName(), messages);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", getName())
                .toString();
    }


    public interface Message {

        String getObjectNameReference();

        String getTaskNameReference();

        default void accept(OswdVisitor visitor) {
            visitor.visitMessage(this);
        }
    }

    public static class ReceiveTaskBuilder extends TaskBuilder<ReceiveTask, ReceiveTaskBuilder> {

        public ReceiveTaskBuilder() {
            super(new ReceiveTask());
        }

        @Override
        protected ReceiveTaskBuilder self() {
            return this;
        }

        public ReceiveTaskBuilder addMessage(String objectNameReference, String taskNameReference) {
            product.messages.add(new Message() {

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
                    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                            .append("objectNameReference", objectNameReference)
                            .append("taskNameReference", taskNameReference)
                            .toString();
                }

            });
            return self();
        }

        public ReceiveTask build() {
            return product.copy();
        }
    }
}
