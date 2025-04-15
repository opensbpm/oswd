package org.opensbpm.oswd

import freemarker.ext.util.WrapperTemplateModel
import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateMethodModelEx
import freemarker.template.TemplateModelException
import org.opensbpm.engine.api.model.definition.StateDefinition.FunctionStateDefinition
import org.opensbpm.engine.api.model.definition.StateDefinition.ReceiveStateDefinition
import org.opensbpm.engine.api.model.definition.StateDefinition.SendStateDefinition
import org.opensbpm.oswd.model.Process
import org.opensbpm.oswd.model.Receive
import org.opensbpm.oswd.model.Send
import org.opensbpm.oswd.model.Task

class Freemarker {

    static void write(Process process, Writer writer) {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_34);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "/org/opensbpm/oswd/templates");

        // Create the root hash. We use a Map here, but it could be a JavaBean too.
        Map<String, Object> root = new HashMap<>();
        root.put("fetchTaskType", new FetchTaskTypeMethod());
        root.put("process", process);

        Template temp = cfg.getTemplate("oswd.ftlh");
        temp.process(root, writer);
    }

    public static class FetchTaskTypeMethod implements TemplateMethodModelEx {

        @Override
        public Object exec(List list) throws TemplateModelException
        {
            Object object = ((WrapperTemplateModel) list.get(0)).getWrappedObject();
            if(Task.isAssignableFrom(object.getClass())) {
                return "Task"
            }else if(Send.isAssignableFrom(object.getClass())){
                return "Send"
            }else if(Receive.isAssignableFrom(object.getClass())){
                return "Receive"
            }else{
                throw new TemplateModelException("Unsupported type: " + object.getClass().getName());
            }
        }
    }
}
