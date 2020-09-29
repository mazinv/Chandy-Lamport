package cz.zcu.kiv.ds.mazin.snapshot;

import cz.zcu.kiv.ds.mazin.messaging.Channel;
import cz.zcu.kiv.ds.mazin.messaging.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Snapshot {
    public final String uuid;
    public final Map<Channel, Boolean> chanels;
    public final long balance;
    private final List<Message> messageList;

    public Snapshot(String uuid, Map<Channel, Boolean> channels, long balance) {
        this.uuid = uuid;
        this.chanels = channels;
        this.balance = balance;
        this.messageList = new ArrayList<>();
    }

    public synchronized void addMessage(Message message) {
        messageList.add(message);
    }

    public synchronized String listMessages() {
        StringBuilder sb = new StringBuilder();
        for(var msg : messageList) {
            sb.append(msg.toString());
            sb.append("\n");
        }

        return sb.toString();
    }
}
