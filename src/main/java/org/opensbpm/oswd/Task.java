package org.opensbpm.oswd;

import org.opensbpm.oswd.AbstractNamed.AbstractBuilder;

public interface Task extends HasName {
    void accept(OswdVisitor visitor);

    public abstract static class TaskBuilder<T extends AbstractNamed & Task, B extends TaskBuilder<T, B>>
            extends AbstractBuilder<T, B> {

        protected TaskBuilder(T product) {
            super(product);
        }

    }
}
