package org.opensbpm.oswd

import org.opensbpm.oswd.model.Message
import org.opensbpm.oswd.model.Object
import org.opensbpm.oswd.model.Process
import org.opensbpm.oswd.model.Receive
import org.opensbpm.oswd.model.Subject
import org.opensbpm.oswd.model.Task
import org.opensbpm.oswd.model.Taskable
import org.opensbpm.oswd.model.Attribute

import static org.apache.groovy.util.BeanUtils.capitalize

class ProcessBuilder extends FactoryBuilderSupport {
//    static ProcessBuilder process(Map<String, Serializable> values, Closure closure){
//        def processBuilder = new ProcessBuilder()
//        processBuilder.invokeMethod('process', [values, closure])
//        return processBuilder;
//    }

    def factory = new ModelFactory(loader: getClass().classLoader)

    protected Factory resolveFactory(name, Map attrs, value) {
        factory
    }

    class ModelFactory extends AbstractFactory {
        ClassLoader loader

        def newInstance(FactoryBuilderSupport fbs, name, value, Map attrs) {
            def clazz = loader.loadClass('org.opensbpm.oswd.model.' + capitalize(name))
            value ? clazz.newInstance(name: value) : clazz.newInstance()
        }

        @Override
        void setChild(FactoryBuilderSupport builder, java.lang.Object parent, java.lang.Object child) {
            if (parent instanceof Process) {
                ((Process) parent).subjects << (Subject)child
            } else if (parent instanceof Subject) {
                ((Subject) parent).tasks << (Taskable) child
            } else if (parent instanceof Task) {
                ((Task) parent).object = (Object) child
            } else if (parent instanceof Object) {
                ((Object) parent).attributes <<  (Attribute) child
            } else if (parent instanceof Receive) {
                ((Receive) parent).messages <<  (Message) child
            }
        }
    }
}
