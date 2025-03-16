package org.opensbpm.oswd;

import java.util.ArrayList;
import java.util.Collection;

import static java.util.Collections.unmodifiableCollection;

public final class Process extends AbstractNamed {

    public static ProcessBuilder builder() {
        return new ProcessBuilder();
    }

    private int version;
    private String description;
    private final Collection<Subject> subjects;

    private Process() {
        subjects = new ArrayList<>();
    }

    private Process(String name, int version, String description, Collection<Subject> subjects) {
        super(name);
        this.version = version;
        this.description = description;
        this.subjects = new ArrayList<>(subjects);
    }

    public int getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    public Collection<Subject> getSubjects() {
        return unmodifiableCollection(subjects);
    }

    public void accept(OswdVisitor visitor) {
        visitor.visitProcess(this);
        getSubjects().forEach(subject -> subject.accept(visitor));
    }

    private Process copy() {
        return new Process(getName(), version, description, subjects);
    }

    public static final class ProcessBuilder extends AbstractBuilder<Process, ProcessBuilder> {

        private ProcessBuilder() {
            super(new Process());
        }

        public ProcessBuilder withVersion(int version) {
            product.version = version;
            return self();
        }

        public ProcessBuilder withDescription(String description) {
            product.description = description;
            return self();
        }

        @Override
        protected ProcessBuilder self() {
            return this;
        }

        public ProcessBuilder addSubject(Subject subject) {
            product.subjects.add(subject);
            return self();
        }

        public Process build() {
            return product.copy();
        }
    }

}
