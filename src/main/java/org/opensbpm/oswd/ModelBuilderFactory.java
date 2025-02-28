package org.opensbpm.oswd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class ModelBuilderFactory {

    public static ProcessBuilder createProcessBuilder() {
        return new ProcessBuilder();
    }

    public static SubjectBuilder createSubjectBuilder() {
        return new SubjectBuilder();
    }

    public static interface ModelBuilder {

    }

    public static class ProcessBuilder implements ModelBuilder {
        private String name;
        private int version;
        private Collection<Subject> subjects = new ArrayList<>();

        public ProcessBuilder withName(String name) {
            this.name = Objects.requireNonNull(name, "Name must not be null");
            return this;
        }

        public ProcessBuilder withVersion(int version) {
            this.version = version;
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

    public static class SubjectBuilder implements ModelBuilder {
        private String name;
        private String roleName;

        public SubjectBuilder withName(String name) {
            this.name = Objects.requireNonNull(name, "Name must not be null");
            return this;
        }

        public SubjectBuilder withRoleName(String name) {
            this.roleName = Objects.requireNonNull(name, "Role name must not be null");
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
            };
        }

    }
}
