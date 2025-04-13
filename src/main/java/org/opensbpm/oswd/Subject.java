package org.opensbpm.oswd;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static java.util.Collections.unmodifiableCollection;
import static java.util.Objects.requireNonNull;

public final class Subject extends AbstractNamed {

    public static SubjectBuilder builder() {
        return new SubjectBuilder();
    }

    private Role role;
    private final Collection<Task> tasks;

    private Subject() {
        tasks = new ArrayList<>();
    }

    private Subject(String name, Role role, Collection<Task> tasks) {
        super(name);
        this.role = Objects.requireNonNull(role, "Role must not be null");
        this.tasks = new ArrayList<>(tasks);
    }

    public Role getRole() {
        return role;
    }

    public Collection<Task> getTasks() {
        return unmodifiableCollection(tasks);
    }

    public void accept(OswdVisitor visitor) {
        visitor.visitSubject(this);
        getRole().accept(visitor);
        getTasks().forEach(task -> task.accept(visitor));
    }

    private Subject copy() {
        return new Subject(getName(), role, tasks);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", getName())
                .append("role", getRole().getName())
                .toString();
    }

    public interface Role extends HasName {
        default void accept(OswdVisitor visitor) {
            visitor.visitRole(this);
        }
    }

    public static class SubjectBuilder extends AbstractBuilder<Subject, SubjectBuilder> {
        public SubjectBuilder() {
            super(new Subject());
        }

        @Override
        protected SubjectBuilder self() {
            return this;
        }

        public SubjectBuilder withRoleName(String name) {
            return withRole(new Role() {
                @Override
                public String getName() {
                    return name;
                }
            });
        }

        public SubjectBuilder withRole(Role role) {
            product.role = requireNonNull(role, "Role name must not be null");
            return self();
        }

        public SubjectBuilder addTask(Task task) {
            product.tasks.add(task);
            return self();
        }

        public Subject build() {
            return product.copy();
        }
    }
}
