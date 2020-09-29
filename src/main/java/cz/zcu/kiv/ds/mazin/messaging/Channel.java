package cz.zcu.kiv.ds.mazin.messaging;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;

public class Channel {
    public ZMQ.Socket sender;
    public ZMQ.Socket receiver;
    public String thisSideIP;
    public String otherSideIP;
    private final Gson gson;
    private long id;

    private static Logger logger = LoggerFactory.getLogger(Channel.class);
    private static long idCounter = 0L;

    public Channel(ZMQ.Socket sender, ZMQ.Socket receiver, String thisSideIP, String otherSideIP) {
        this.sender = sender;
        this.receiver = receiver;
        this.thisSideIP = thisSideIP;
        this.otherSideIP = otherSideIP;
        this.gson = new Gson();
        this.id = idCounter++;
    }

    public void send(Message message) {
        var jsonMsg = gson.toJson(message);
        logger.info("{} --- Sending to {}: {}", thisSideIP, otherSideIP, message);
        sender.send(jsonMsg);
    }

    public Message receive() {
        var jsonMsg = receiver.recvStr();
        Message message = gson.fromJson(jsonMsg, Message.class);
        logger.info("{} --- Received from {}: {}", thisSideIP, otherSideIP, message);

        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Channel channel = (Channel) o;

        return id == channel.id;
    }
}
