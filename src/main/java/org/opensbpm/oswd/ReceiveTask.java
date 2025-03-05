package org.opensbpm.oswd;

import java.util.Collection;

public interface ReceiveTask extends Task {

    Collection<Message> getMessages();

    interface Message{

        String getObjectNameReference();
        String taskNameReference();
    }
}
