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
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static spark.Spark.*;

public class BankingApp {
    private static Logger logger = LoggerFactory.getLogger(BankingApp.class);
    private static Balance balance = new Balance();
    private static SnapshotService snapshotService;

    public static void main(String[] args) {
        String[] strs = {"a", "b", "c"};
        HashSet<String> set = new HashSet<String>(Arrays.asList(strs));
        List<String> list = set.stream().filter(s -> !s.equals("a")).collect(Collectors.toList());

        try {
            List<Pair<String, String>> topology = loadTopology(args[0], Integer.parseInt(args[1]));
            String nodeIP = topology.get(0).getValue0().split(":")[0];

            File snapshotDir = new File(nodeIP);
            if (!snapshotDir.exists())
                snapshotDir.mkdir();

            snapshotService = new SnapshotService(nodeIP, balance);

            ZMQ.Context context = ZMQ.context(1);

            for (Pair<String, String> pair : topology) {
                ZMQ.Socket receiverSocket = context.socket(SocketType.PAIR);
                ZMQ.Socket senderSocket = context.socket(SocketType.PAIR);
                boolean statRecv = receiverSocket.bind("tcp://" + pair.getValue0());
                logger.info("Receiver bound to {}", pair.getValue0());
                String connectString = "tcp://" + pair.getValue1();
                boolean statSend = senderSocket.connect(connectString);
                logger.info("Sender connected to {}", pair.getValue1());

                String otherSideIP = pair.getValue1().split(":")[0];

                Channel channel = new Channel(senderSocket, receiverSocket, nodeIP, otherSideIP);
                snapshotService.addChannel(channel);

                new Thread(new Receiver(channel, balance, snapshotService)).start();
                new Thread(new Sender(channel, balance, snapshotService)).start();
            }
        } catch (IOException e) {
            logger.error("Error while reading topology file {}", args[0]);
        }

        get("/marker", (req, res) -> {
            snapshotService.startSnapshot(UUID.randomUUID().toString(), null);
            return "Marker send";
        });
    }

    public static List<Pair<String, String>> loadTopology(String topologyFile, int nodeNumber) throws IOException {
        List<Pair<String, String>> result = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(topologyFile));
        String line = lines.get(nodeNumber);

        String[] routes = line.split(";");

        for(String route : routes) {
            String[] ips = route.split("->");
            Pair<String, String> pair = new Pair<>(ips[0], ips[1]);
            result.add(pair);
        }

        return result;
    }
}
