package cz.zcu.kiv.ds.mazin;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.*;

public class BankingApp {
    private static Logger logger = LoggerFactory.getLogger(BankingApp.class);

    public static void main(String[] args) {
        try {
            var topology = loadTopology(args[0], Integer.parseInt(args[1]));
            var context = ZMQ.context(1);

            for (var pair : topology) {
                var receiverSocket = context.socket(SocketType.PAIR);
                var senderSocket = context.socket(SocketType.PAIR);
                var statRecv = receiverSocket.bind("tcp://" + pair.getValue0());
                logger.info("Receiver bound to {}", pair.getValue0());
                var statSend = senderSocket.connect("tcp://" + pair.getValue1());
                logger.info("Sender connected to {}", pair.getValue1());
            }
        } catch (IOException e) {
            logger.error("Error while reading topology file {}", args[0]);
        }

        get("/marker", (req, res) -> "Marker send");
    }

    public static List<Pair<String, String>> loadTopology(String topologyFile, int nodeNumber) throws IOException {
        var result = new ArrayList<Pair<String, String>>();
        var lines = Files.readAllLines(Path.of(topologyFile));
        var line = lines.get(nodeNumber);

        var routes = line.split(";");

        for(var route : routes) {
            var ips = route.split("#");
            var pair = new Pair<String, String>(ips[0], ips[1]);
            result.add(pair);
        }

        return result;
    }
}
