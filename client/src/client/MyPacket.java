package client;

import java.io.Serializable;

public class MyPacket implements Serializable {

    private String command;
    private String arguments;
    private Object attachment;

    public MyPacket(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public String getArguments() {
        return arguments;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    public Object getAttachment() {
        return attachment;
    }

    public void setAttachment(Object attachment) {
        this.attachment = attachment;
    }
}
