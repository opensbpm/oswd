package org.opensbpm.oswd;

import java.util.Collection;

public interface ReceiveTask extends Task {

    Collection<Message> getMessages();

    default void accept(OswdVisitor visitor) {
        visitor.visitReceiveTask(this);

        getMessages().forEach(message -> message.accept(visitor));
    }

    interface Message {

        String getObjectNameReference();

        String getTaskNameReference();

        default void accept(OswdVisitor visitor) {
            visitor.visitMessage(this);
        }

    }
}
