package org.opensbpm.oswd.parser;

import org.opensbpm.oswd.*;
import org.opensbpm.oswd.Process;
import org.opensbpm.oswd.parser.OswdParser.AttributeContext;
import org.opensbpm.oswd.parser.OswdParser.ProcessContext;
import org.opensbpm.oswd.parser.OswdParser.SubjectContext;
import org.opensbpm.oswd.parser.OswdParser.ShowContext;
import org.opensbpm.oswd.parser.OswdParser.SendContext;
import org.opensbpm.oswd.parser.OswdParser.ReceiveContext;
import org.opensbpm.oswd.parser.OswdParser.ObjectContext;
import org.opensbpm.oswd.Attribute.AttributeBuilder;
import org.opensbpm.oswd.Process.ProcessBuilder;
import org.opensbpm.oswd.Subject.SubjectBuilder;
import org.opensbpm.oswd.ShowTask.ShowTaskBuilder;
import org.opensbpm.oswd.SendTask.SendTaskBuilder;
import org.opensbpm.oswd.ReceiveTask.ReceiveTaskBuilder;
import org.opensbpm.oswd.BusinessObject.BusinessObjectBuilder;

public class ContextStackFactory {

    public static StackItem<ProcessBuilder, ProcessContext> processItem(ProcessContext ctx) {
        return new AbstractStackItem<>(ctx) {

            @Override
            public ProcessBuilder createBuilder() {
                return Process.builder();
            }
        };
    }

    public static StackItem<SubjectBuilder, SubjectContext> subjectItem(SubjectContext ctx) {
        return new AbstractStackItem<>(ctx) {

            @Override
            public SubjectBuilder createBuilder() {
                return Subject.builder();
            }

        };
    }

    public static StackItem<ShowTaskBuilder, ShowContext> showItem(ShowContext ctx) {
        return new AbstractStackItem<>(ctx) {

            @Override
            public ShowTaskBuilder createBuilder() {
                return ShowTask.builder();
            }
        };
    }

    public static StackItem<SendTaskBuilder, SendContext> sendItem(SendContext ctx) {
        return new AbstractStackItem<>(ctx) {

            @Override
            public SendTaskBuilder createBuilder() {
                return SendTask.builder();
            }

        };
    }

    public static StackItem<ReceiveTaskBuilder, ReceiveContext> receiveItem(ReceiveContext ctx) {
        return new AbstractStackItem<>(ctx) {

            @Override
            public ReceiveTaskBuilder createBuilder() {
                return ReceiveTask.builder();
            }

        };
    }

    public static StackItem<BusinessObjectBuilder, ObjectContext> objectItem(ObjectContext ctx) {
        return new AbstractStackItem<>(ctx) {

            @Override
            public BusinessObjectBuilder createBuilder() {
                return BusinessObject.builder();
            }

        };
    }

    public static StackItem<AttributeBuilder, AttributeContext> attributeItem(AttributeContext ctx) {
        return new AbstractStackItem<>(ctx) {

            @Override
            public AttributeBuilder createBuilder() {
                return Attribute.builder();
            }

        };
    }

    private ContextStackFactory() {
        //avoid instantiation
    }
}
