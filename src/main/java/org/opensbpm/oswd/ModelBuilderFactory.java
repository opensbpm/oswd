package org.opensbpm.oswd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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

    public static interface ModelBuilder {

    }

    public static abstract class AbstractBuilder<B extends AbstractBuilder<B>> implements ModelBuilder {
        protected String name;

        protected abstract B self();

        public final B withName(String name) {
            this.name = Objects.requireNonNull(name, "Name must not be null");
            return self();
        }


    }

    public static class ProcessBuilder extends AbstractBuilder<ProcessBuilder> {
        private int version;
        private Collection<Subject> subjects = new ArrayList<>();

        public ProcessBuilder withVersion(int version) {
            this.version = version;
            return this;
        }

        @Override
        protected ProcessBuilder self() {
            return this;
        }

        public ProcessBuilder addSubject(Subject subject) {
            this.subjects.add(subject);
            return this;
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

    public static class SubjectBuilder extends AbstractBuilder<SubjectBuilder> {
        private String roleName;
        private List<Task> tasks = new ArrayList<>();

        @Override
        protected SubjectBuilder self() {
            return this;
        }

        public SubjectBuilder withRoleName(String name) {
            this.roleName = Objects.requireNonNull(name, "Role name must not be null");
            return this;
        }

        public SubjectBuilder addTask(Task task) {
            this.tasks.add(task);
            return this;
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

    public static abstract class AbstractTaskBuilder<B extends AbstractTaskBuilder<B>> extends AbstractBuilder<B>{

        public Task build() {
            return new Task() {

                @Override
                public String getName() {
                    return name;
                }
            };
        }
    }

    public static class ShowTaskBuilder extends AbstractTaskBuilder<ShowTaskBuilder>{

        @Override
        protected ShowTaskBuilder self() {
            return this;
        }

        public Task build() {
            return new Task() {

                @Override
                public String getName() {
                    return name;
                }
            };
        }
    }

    public static class SendTaskBuilder extends AbstractTaskBuilder<SendTaskBuilder>{

        @Override
        protected SendTaskBuilder self() {
            return this;
        }

        public Task build() {
            return new Task() {

                @Override
                public String getName() {
                    return name;
                }
            };
        }
    }

    public static class ReceiveTaskBuilder extends AbstractTaskBuilder<ReceiveTaskBuilder>{

        @Override
        protected ReceiveTaskBuilder self() {
            return this;
        }

        public Task build() {
            return new Task() {

                @Override
                public String getName() {
                    return name;
                }
            };
        }
    }
}
