package org.opensbpm.oswd.matchers;

import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.opensbpm.oswd.Process;
import org.opensbpm.oswd.ShowTask;
import org.opensbpm.oswd.SendTask;
import org.opensbpm.oswd.Subject;
import org.opensbpm.oswd.Task;
import org.opensbpm.oswd.ReceiveTask;
import org.opensbpm.oswd.ReceiveTask.Message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.List.of;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.opensbpm.engine.utils.StreamUtils.oneOrMoreAsList;

public class OswdMatchers {

    public static Matcher<Process> isProcess(String name, int version, Matcher<Process>... additional) {
        ArrayList<Matcher<? super Process>> matchers = new ArrayList<>(List.of(
                instanceOf(Process.class),
                isProcessName(name),
                isVersion(version)
        ));
        if (additional != null) {
            matchers.addAll(asList(additional));
        }
        return allOf(matchers);
    }

    public static CustomTypeSafeMatcher<Process> isProcessName(String name) {
        return new CustomTypeSafeMatcher<>("name") {
            @Override
            protected boolean matchesSafely(Process process) {
                return is(name).matches(process.getName());
            }
        };
    }

    public static CustomTypeSafeMatcher<Process> isVersion(int version) {
        return new CustomTypeSafeMatcher<>("version") {
            @Override
            protected boolean matchesSafely(Process process) {
                return is(version).matches(process.getVersion());
            }
        };
    }

    public static Matcher<Process> isDescription(String description) {
        return isDescription(is(description));
    }

    public static Matcher<Process> isDescription(Matcher<String> descriptionMatcher) {
        return new CustomTypeSafeMatcher<>("description") {
            @Override
            protected boolean matchesSafely(Process process) {
                return descriptionMatcher.matches(process.getDescription());
            }
        };
    }

    public static Matcher<Process> containsSubjects(Matcher<Subject> matcher, Matcher<Subject>... additionals) {
        List<Matcher<? super Subject>> matchers = oneOrMoreAsList(matcher, additionals);
        return new CustomTypeSafeMatcher<>("subjects") {
            @Override
            protected boolean matchesSafely(Process process) {
                return containsInAnyOrder(matchers).matches(process.getSubjects());
            }

            @Override
            protected void describeMismatchSafely(Process item, Description mismatchDescription) {
                mismatchDescription.appendText("was ");
                mismatchDescription.appendValueList("", ", ", "", item.getSubjects());
            }
        };
    }

    public static Matcher<Subject> isSubject(String name, String role) {
        return isSubject(name, role, null);
    }

    public static Matcher<Subject> isSubject(String name, String role, Matcher<? super Subject>... additionals) {
        ArrayList<Matcher<? super Subject>> matchers = new ArrayList<>(List.of(
                instanceOf(Subject.class),
                isSubjectName(name),
                isRoleName(role)
        ));
        if (additionals != null) {
            matchers.addAll(asList(additionals));
        }
        return allOf(matchers);
    }

    public static CustomTypeSafeMatcher<Subject> isSubjectName(String name) {
        return new CustomTypeSafeMatcher<>("subject with name " + name) {
            @Override
            protected boolean matchesSafely(Subject subject) {
                return is(name).matches(subject.getName());
            }
        };
    }

    public static CustomTypeSafeMatcher<Subject> isRoleName(String name) {
        return new CustomTypeSafeMatcher<>("subject with role name " + name) {
            @Override
            protected boolean matchesSafely(Subject subject) {
                return is(name).matches(subject.getRole().getName());
            }
        };
    }

    public static CustomTypeSafeMatcher<Subject> hasTasksSize(int size) {
        return new CustomTypeSafeMatcher<>("tasks size " + size) {
            @Override
            protected boolean matchesSafely(Subject subject) {
                return hasSize(size).matches(subject.getTasks());
            }
        };
    }

    public static CustomTypeSafeMatcher<Task> isTask(Matcher<? super Task> matcher, Matcher<? super Task>... additionals) {
        ArrayList<Matcher<? super Task>> matchers = new ArrayList<>(of(matcher));
        matchers.addAll(asList(additionals));

        StringDescription description = new StringDescription();
        allOf(matchers).describeTo(description);
        return new CustomTypeSafeMatcher<>("Tasks " + description) {
            @Override
            protected boolean matchesSafely(Task task) {
                return allOf(matchers).matches(task);
            }
        };
    }

    public static CustomTypeSafeMatcher<Task> isTaskName(String name) {
        return new CustomTypeSafeMatcher<>("Task with name " + name) {
            @Override
            protected boolean matchesSafely(Task task) {
                return is(name).matches(task.getName());
            }
        };
    }

    public static Matcher<ShowTask> isShowTask(String name) {
        return allOf(
                instanceOf(ShowTask.class),
                isTaskName(name),
                isShowProceedTo(nullValue(String.class))
        );
    }

    public static Matcher<ShowTask> isShowTask(String name, String proceedTo) {
        return allOf(
                instanceOf(ShowTask.class),
                isTaskName(name),
                isShowProceedTo(proceedTo)
        );
    }

    public static CustomTypeSafeMatcher<? super ShowTask> isObjectName(String name) {
        return new CustomTypeSafeMatcher<>("ShowTask with name " + name) {
            @Override
            protected boolean matchesSafely(ShowTask task) {
                return is(name).matches(task.getBusinessObject().getName());
            }
        };
    }

    public static CustomTypeSafeMatcher<ShowTask> isShowProceedTo(String name) {
        return isShowProceedTo(is(name));
    }

    public static CustomTypeSafeMatcher<ShowTask> isShowProceedTo(Matcher<String> matcher) {
        return new CustomTypeSafeMatcher<>("proceedTo " + matcher) {
            @Override
            protected boolean matchesSafely(ShowTask task) {
                return matcher.matches(task.getProceedTo());
            }
        };
    }

    public static Matcher<SendTask> isSendTask(String name, String object, String receiver, String proceedTo) {
        return allOf(
                instanceOf(SendTask.class),
                isTaskName(name),
                isObjectNameReference(object),
                isReceiverSubjectName(receiver),
                isSendProceedTo(proceedTo)
        );
    }

    public static CustomTypeSafeMatcher<? super SendTask> isObjectNameReference(String name) {
        return new CustomTypeSafeMatcher<>("SendTask with object name reference " + name) {
            @Override
            protected boolean matchesSafely(SendTask task) {
                return is(name).matches(task.getObjectNameReference());
            }
        };
    }

    public static CustomTypeSafeMatcher<? super SendTask> isReceiverSubjectName(String name) {
        return new CustomTypeSafeMatcher<>("SendTask with receiver subject name " + name) {
            @Override
            protected boolean matchesSafely(SendTask task) {
                return is(name).matches(task.getReceiverSubjectName());
            }
        };
    }

    public static CustomTypeSafeMatcher<SendTask> isSendProceedTo(String name) {
        return new CustomTypeSafeMatcher<>("SendTask with name " + name) {
            @Override
            protected boolean matchesSafely(SendTask task) {
                return is(name).matches(task.getProceedTo());
            }
        };
    }

    public static Matcher<ReceiveTask> isReceiveTask(String name, Matcher<? super Message> matcher, Matcher<? super Message>... additionals) {
        return allOf(
                instanceOf(ReceiveTask.class),
                isTaskName(name),
                containsMessages(matcher, additionals)
        );
    }

    public static CustomTypeSafeMatcher<ReceiveTask> containsMessages(Matcher<? super Message> matcher, Matcher<? super Message>... additionals) {
        List<Matcher<? super Message>> matchers = oneOrMoreAsList(matcher, additionals);

        StringDescription description = new StringDescription();
        allOf(matchers).describeTo(description);
        return new CustomTypeSafeMatcher<>("tasks " + description) {
            @Override
            protected boolean matchesSafely(ReceiveTask receiveTask) {
                return contains(matchers).matches(receiveTask.getMessages());
            }
        };
    }

    public static Matcher<Message> isMessage(String objectName, String taskName) {
        return allOf(
                isMessageObjectNameReference(objectName),
                isMessageTaskNameReference(taskName)
        );
    }

    public static CustomTypeSafeMatcher<? super Message> isMessageObjectNameReference(String name) {
        return new CustomTypeSafeMatcher<>("Message with object name reference " + name) {
            @Override
            protected boolean matchesSafely(Message task) {
                return is(name).matches(task.getObjectNameReference());
            }
        };
    }

    public static CustomTypeSafeMatcher<? super Message> isMessageTaskNameReference(String name) {
        return new CustomTypeSafeMatcher<>("Message with task name reference " + name) {
            @Override
            protected boolean matchesSafely(Message task) {
                return is(name).matches(task.getTaskNameReference());
            }
        };
    }


    public OswdMatchers() {
        //avoid instantiation
    }
}
