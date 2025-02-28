package org.opensbpm.oswd;

import org.opensbpm.oswd.OswdParser.ProcessContext;
import org.opensbpm.oswd.OswdParser.SubjectContext;
import org.opensbpm.oswd.OswdParser.TaskContext;
import org.opensbpm.oswd.ModelBuilderFactory.ProcessBuilder;
import org.opensbpm.oswd.ModelBuilderFactory.SubjectBuilder;
import org.opensbpm.oswd.ModelBuilderFactory.TaskBuilder;

public class ContextStackFactory {

    public static StackItem<ProcessBuilder, ProcessContext> processItem(ProcessContext ctx) {
        return new AbstractStackItem<>(ctx) {

            @Override
            public ProcessBuilder createBuilder() {
                return ModelBuilderFactory.createProcessBuilder();
            }

        };
    }

    public static StackItem<SubjectBuilder, SubjectContext> subjectItem(SubjectContext ctx) {
        return new AbstractStackItem<>(ctx) {

            @Override
            public SubjectBuilder createBuilder() {
                return ModelBuilderFactory.createSubjectBuilder();
            }

        };
    }

    public static StackItem<TaskBuilder, TaskContext> taskItem(TaskContext ctx) {
        return new AbstractStackItem<>(ctx) {

            @Override
            public TaskBuilder createBuilder() {
                return ModelBuilderFactory.createTaskBuilder();
            }

        };
    }

}
