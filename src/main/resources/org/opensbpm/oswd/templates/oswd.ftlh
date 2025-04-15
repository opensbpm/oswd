#!groovy
import org.opensbpm.oswd.ProcessBuilder

import static org.opensbpm.oswd.model.AttributeType.DATE
import static org.opensbpm.oswd.model.AttributeType.TEXT

new ProcessBuilder().process(name: '${process.name}', version: ${process.version}) {
    <#list process.subjects as subject>
    subject(name: '${subject.name}', role: '${subject.role}') {
            <#list subject.tasks as task>
                <#switch fetchTaskType(task)>
                    <#on "Task">
        task(name: '${task.name}') {
                    <#if task.object??>
                        <@object task.object/>
                    </#if>
                    <#list task.proceedTos as proceedTo>
            proceedTo('${proceedTo.name}'){
                        <#if proceedTo.object??>
                            <@object proceedTo.object/>
                        </#if>
            }
                    </#list>
        }
                    <#on "Send">
        send(name: '${task.name}',
            message: '${task.message}',
            receiver: '${task.receiver}'
            <#if task.proceedTo??>
            ,proceedTo: '${task.proceedTo}'
            </#if>
            )
                    <#on "Receive">
        receive(name: '${task.name}') {
                        <#list task.messages as message>
            message(object: '${message.object}', proceedTo: '${message.proceedTo}')
                        </#list>
        }
                </#switch>
            </#list>
    }
    </#list>
}

<#macro object object>
    object(name: '${object.name}') {
    <#list object.attributes as attribute>
        attribute(name: '${attribute.name}', type: ${attribute.type} ${attribute.required?string(', required: true','')} ${attribute.readonly?string(', readonly: true','')})
    </#list>
    }
</#macro>
