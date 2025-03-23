package org.opensbpm.oswd.convert;

import org.opensbpm.engine.api.model.definition.ProcessDefinition;
import org.opensbpm.engine.api.model.definition.StateDefinition;
import org.opensbpm.engine.api.model.definition.StateDefinitionVisitor;
import org.opensbpm.engine.api.model.definition.StateDefinition.FunctionStateDefinition;
import org.opensbpm.engine.api.model.definition.StateDefinition.ReceiveStateDefinition;
import org.opensbpm.engine.api.model.definition.StateDefinition.SendStateDefinition;
import org.opensbpm.engine.api.model.definition.SubjectDefinition;
import org.opensbpm.engine.api.model.definition.SubjectDefinition.UserSubjectDefinition;
import org.opensbpm.oswd.*;
import org.opensbpm.oswd.Process;
import org.opensbpm.oswd.Process.ProcessBuilder;
import org.opensbpm.oswd.ShowTask.ShowTaskBuilder;
import org.opensbpm.oswd.Subject.SubjectBuilder;
import org.opensbpm.oswd.Task.TaskBuilder;

public class ProcessDefinitionConverter {

    public Process convert(ProcessDefinition processType) {
        ProcessBuilder processBuilder = Process.builder()
                .withName(processType.getName())
                .withVersion(processType.getVersion())
                .withDescription(processType.getDescription());

        for(SubjectDefinition subject : processType.getSubjects()) {
            processBuilder.addSubject(convertSubject(subject));
        }
        return processBuilder.build();
    }

    private Subject convertSubject(SubjectDefinition subjectDefinition) {
        SubjectBuilder subjectBuilder = Subject.builder()
                .withName(subjectDefinition.getName());
        if(subjectDefinition instanceof UserSubjectDefinition){
            String roleName = ((UserSubjectDefinition) subjectDefinition).getRoles().iterator().next();
            subjectBuilder.withRoleName(roleName);
        }
        for(StateDefinition stateDefinition : subjectDefinition.getStates()) {
            Task task = stateDefinition.accept(new StateDefinitionVisitor<Task>() {

                @Override
                public Task visitFunctionState(FunctionStateDefinition functionStateDefinition) {
                    return ShowTask.builder()
                            .withName(functionStateDefinition.getName())
                            .build();
                }

                @Override
                public Task visitReceiveState(ReceiveStateDefinition receiveStateDefinition) {
                    return ReceiveTask.builder()
                            .withName(receiveStateDefinition.getName())
                            .build();
                }

                @Override
                public Task visitSendState(SendStateDefinition sendStateDefinition) {
                    return SendTask.builder()
                            .withName(sendStateDefinition.getName())
                            .build();
                }
            });
            subjectBuilder.addTask(task);
        }
        return subjectBuilder.build();
    }

}
