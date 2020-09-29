package cz.zcu.kiv.ds.mazin;

import cz.zcu.kiv.ds.mazin.messaging.Channel;
import cz.zcu.kiv.ds.mazin.messaging.Receiver;
import cz.zcu.kiv.ds.mazin.messaging.Sender;
import cz.zcu.kiv.ds.mazin.snapshot.SnapshotService;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static spark.Spark.*;

public class BankingApp {
    private static Logger logger = LoggerFactory.getLogger(BankingApp.class);
    private static Balance balance = new Balance();
    private static SnapshotService snapshotService;

    public static void main(String[] args) {
        String[] strs = {"a", "b", "c"};
        var set = new HashSet<String>(Arrays.asList(strs));
        var list = set.stream().filter(s -> !s.equals("a")).collect(Collectors.toList());

        try {
            var topology = loadTopology(args[0], Integer.parseInt(args[1]));
            var nodeIP = topology.get(0).getValue0().split(":")[0];

            File snapshotDir = new File(nodeIP);
            if (!snapshotDir.exists())
                snapshotDir.mkdir();

            snapshotService = new SnapshotService(nodeIP);

            var context = ZMQ.context(1);

            for (var pair : topology) {
                var receiverSocket = context.socket(SocketType.PAIR);
                var senderSocket = context.socket(SocketType.PAIR);
                var statRecv = receiverSocket.bind("tcp://" + pair.getValue0());
                logger.info("Receiver bound to {}", pair.getValue0());
                var statSend = senderSocket.connect("tcp://" + pair.getValue1());
                logger.info("Sender connected to {}", pair.getValue1());

                var channel = new Channel(senderSocket, receiverSocket, nodeIP);
                snapshotService.addChannel(channel);

                new Thread(new Receiver(channel, balance, snapshotService)).start();
                new Thread(new Sender(channel, balance, snapshotService)).start();
            }
        } catch (IOException e) {
            logger.error("Error while reading topology file {}", args[0]);
        }

        get("/marker", (req, res) -> {
            return "Marker send";
        });
    }

    public static List<Pair<String, String>> loadTopology(String topologyFile, int nodeNumber) throws IOException {
        var result = new ArrayList<Pair<String, String>>();
        var lines = Files.readAllLines(Path.of(topologyFile));
        var line = lines.get(nodeNumber);

        var routes = line.split(";");

        for(var route : routes) {
            var ips = route.split("#");
            var pair = new Pair<>(ips[0], ips[1]);
            result.add(pair);
        }

        return result;
    }
}
