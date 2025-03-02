package org.opensbpm.oswd.parser;

import org.opensbpm.oswd.ModelBuilderFactory;
import org.opensbpm.oswd.StackItem;
import org.opensbpm.oswd.parser.OswdParser.AttributeContext;
import org.opensbpm.oswd.parser.OswdParser.ProcessContext;
import org.opensbpm.oswd.parser.OswdParser.SubjectContext;
import org.opensbpm.oswd.parser.OswdParser.ShowContext;
import org.opensbpm.oswd.parser.OswdParser.SendContext;
import org.opensbpm.oswd.parser.OswdParser.ReceiveContext;
import org.opensbpm.oswd.parser.OswdParser.ObjectContext;
import org.opensbpm.oswd.ModelBuilderFactory.AttributeBuilder;
import org.opensbpm.oswd.ModelBuilderFactory.ProcessBuilder;
import org.opensbpm.oswd.ModelBuilderFactory.SubjectBuilder;
import org.opensbpm.oswd.ModelBuilderFactory.ShowTaskBuilder;
import org.opensbpm.oswd.ModelBuilderFactory.SendTaskBuilder;
import org.opensbpm.oswd.ModelBuilderFactory.ReceiveTaskBuilder;
import org.opensbpm.oswd.ModelBuilderFactory.BusinessObjectBuilder;

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

    public static StackItem<ShowTaskBuilder, ShowContext> showItem(ShowContext ctx) {
        return new AbstractStackItem<>(ctx) {

            @Override
            public ShowTaskBuilder createBuilder() {
                return ModelBuilderFactory.createShowTaskBuilder();
            }

        };
    }

    public static StackItem<SendTaskBuilder, SendContext> sendItem(SendContext ctx) {
        return new AbstractStackItem<>(ctx) {

            @Override
            public SendTaskBuilder createBuilder() {
                return ModelBuilderFactory.createSendTaskBuilder();
            }

        };
    }

    public static StackItem<ReceiveTaskBuilder, ReceiveContext> receiveItem(ReceiveContext ctx) {
        return new AbstractStackItem<>(ctx) {

            @Override
            public ReceiveTaskBuilder createBuilder() {
                return ModelBuilderFactory.createReceiveTaskBuilder();
            }

        };
    }

    public static StackItem<BusinessObjectBuilder, ObjectContext> objectItem(ObjectContext ctx) {
        return new AbstractStackItem<>(ctx) {

            @Override
            public BusinessObjectBuilder createBuilder() {
                return ModelBuilderFactory.createBusinessObjectBuilder();
            }

        };
    }

    public static StackItem<AttributeBuilder, AttributeContext> attributeItem(AttributeContext ctx) {
        return new AbstractStackItem<>(ctx) {

            @Override
            public AttributeBuilder createBuilder() {
                return ModelBuilderFactory.createAttributeBuilder();
            }

        };
    }

}
