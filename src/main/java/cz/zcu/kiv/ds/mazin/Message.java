package cz.zcu.kiv.ds.mazin;

public class Message {
    public MessageType type;
    public String data;

    public Message(MessageType type, String data) {
        this.type = type;
        this.data = data;
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", data='" + data + '\'' +
                '}';
    }
}
