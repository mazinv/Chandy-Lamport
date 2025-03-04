package cz.zcu.kiv.ds.mazin.snapshot;

import cz.zcu.kiv.ds.mazin.messaging.Channel;
import cz.zcu.kiv.ds.mazin.messaging.Message;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Snapshot {
    public final String uuid;
    public final Map<Channel, Boolean> chanels;
    public final long balance;
    private final List<Pair<Channel, Message>> messageList;

    public Snapshot(String uuid, Map<Channel, Boolean> channels, long balance) {
        this.uuid = uuid;
        this.chanels = channels;
        this.balance = balance;
        this.messageList = new ArrayList<>();
    }

    public synchronized void addMessage(Message message, Channel channel) {
        messageList.add(new Pair<>(channel, message));
    }

    public synchronized String listMessages() {
        StringBuilder sb = new StringBuilder();

        this.chanels.forEach((channel, isEmpty) -> {
            sb.append("\n")
                    .append("----- Channel for ")
                    .append(channel.otherSideIP)
                    .append(" -----")
                    .append("\n");

            this.messageList.forEach(pair -> {
                if(pair.getValue0().equals(channel))
                    sb.append(pair.getValue1())
                            .append("\n");
            });
        });

        return sb.toString();
    }
}
