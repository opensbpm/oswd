package org.opensbpm.oswd.matchers;

import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.opensbpm.oswd.ShowTask;
import org.opensbpm.oswd.SendTask;
import org.opensbpm.oswd.Subject;
import org.opensbpm.oswd.Task;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;

public class OswdMatchers {

    private static CustomTypeSafeMatcher<Subject> isSubject(Matcher<? super Subject> matcher, Matcher<? super Subject>... additionals) {
        ArrayList<Matcher<? super Subject>> matchers = new ArrayList<>(List.of(matcher));
        matchers.addAll(asList(additionals));

        StringDescription description = new StringDescription();
        allOf(matchers).describeTo(description);
        return new CustomTypeSafeMatcher<>("subjects " + description.toString()) {
            @Override
            protected boolean matchesSafely(Subject subject) {
                return allOf(matchers).matches(subject);
            }
        };
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

    public static CustomTypeSafeMatcher<Subject> containsTasks(Matcher<? super Task> matcher, Matcher<? super Task>... additionals) {
        ArrayList<Matcher<? super Task>> matchers = new ArrayList<>(List.of(matcher));
        matchers.addAll(asList(additionals));

        StringDescription description = new StringDescription();
        allOf(matchers).describeTo(description);
        return new CustomTypeSafeMatcher<>("tasks " + description.toString()) {
            @Override
            protected boolean matchesSafely(Subject subject) {
                return contains(matchers).matches(subject.getTasks());
            }
        };
    }

    public static CustomTypeSafeMatcher<Task> isTask(Matcher<? super Task> matcher, Matcher<? super Task>... additionals) {
        ArrayList<Matcher<? super Task>> matchers = new ArrayList<>(List.of(matcher));
        matchers.addAll(asList(additionals));

        StringDescription description = new StringDescription();
        allOf(matchers).describeTo(description);
        return new CustomTypeSafeMatcher<>("Tasks " + description.toString()) {
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

    public static CustomTypeSafeMatcher<? super ShowTask> isObjectName(String name) {
        return new CustomTypeSafeMatcher<>("ShowTask with name " + name) {
            @Override
            protected boolean matchesSafely(ShowTask task) {
                return is(name).matches(task.getBusinessObject().getName());
            }
        };
    }

    public static CustomTypeSafeMatcher<ShowTask> isShowProceedTo(String name) {
        return new CustomTypeSafeMatcher<>("ShowTask with name " + name) {
            @Override
            protected boolean matchesSafely(ShowTask task) {
                return is(name).matches(task.getProceedTo());
            }
        };
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



    public OswdMatchers() {
        //avoid instantiation
    }
}
