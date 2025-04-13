package org.opensbpm.oswd.model

class Send implements Taskable{
    String name
    String message
    String receiver
    String proceedTo


    @Override
    public String toString() {
        return "Send{" +
                "name='" + name + '\'' +
                ", message='" + message + '\'' +
                ", receiver='" + receiver + '\'' +
                ", proceedTo='" + proceedTo + '\'' +
                '}';
    }
}
