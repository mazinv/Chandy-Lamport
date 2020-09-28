package cz.zcu.kiv.ds.mazin;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;

public class Channel {
    public ZMQ.Socket sender;
    public ZMQ.Socket receiver;
    public String otherSideIP;
    private final Gson gson;

    private static Logger logger = LoggerFactory.getLogger(Channel.class);

    public Channel(ZMQ.Socket sender, ZMQ.Socket receiver, String otherSideIP) {
        this.sender = sender;
        this.receiver = receiver;
        this.otherSideIP = otherSideIP;
        this.gson = new Gson();
    }

    public void send(Message message) {
        var jsonMsg = gson.toJson(message);
        logger.info("Sending to {}: {}", otherSideIP, message);
        sender.send(jsonMsg);
    }

    public Message receive() {
        var jsonMsg = receiver.recvStr();
        Message message = gson.fromJson(jsonMsg, Message.class);
        logger.info("Received from {}: {}", otherSideIP, message);

        return message;
    }
}
