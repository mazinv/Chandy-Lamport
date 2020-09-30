package cz.zcu.kiv.ds.mazin.snapshot;

import cz.zcu.kiv.ds.mazin.Balance;
import cz.zcu.kiv.ds.mazin.messaging.Channel;
import cz.zcu.kiv.ds.mazin.messaging.Message;
import cz.zcu.kiv.ds.mazin.messaging.MessageType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class SnapshotService {
    private Map<String, Snapshot> snapshots;
    private String snapshotDir;
    private Set<Channel> channels;
    private Balance balance;

    public SnapshotService(String snapshotDir, Balance balance) {
        this.snapshotDir = snapshotDir;
        this.snapshots = new HashMap<>();
        this.channels = new HashSet<>();
        this.balance = balance;
    }

    public synchronized Snapshot startSnapshot(String uuid, Channel channel) {
        Map<Channel, Boolean> channels = new HashMap<>();
        this.channels.forEach(c -> {
            if(c == channel)
                channels.put(c, true); //empty channel - if marker came from some channel
            else
                channels.put(c, false); //not empty channel - if marker was created by this process
        });
        var snapshot = new Snapshot(uuid, channels, balance.getBalance());
        snapshots.put(uuid, snapshot);

        sendMarkers(channels, uuid);

        return snapshot;
    }

    private synchronized void sendMarkers(Map<Channel, Boolean> channels, String uuid) {
        channels.forEach((channel, isEmpty) -> {
            channel.send(new Message(MessageType.MARKER, uuid));
        });
    }

    private synchronized boolean checkCompletedSnapshot(Snapshot snapshot) {
        for(var entry : snapshot.chanels.entrySet()) {
            if(!entry.getValue()) //channel is not empty
                return false;
        }

        return true; //all channels are empty
    }

    public synchronized void recordMessage(Message message, Channel channel) {
        if(message.type == MessageType.MARKER) {
            Snapshot snapshot = null;
            if(this.snapshots.containsKey(message.data)) { //there is snapshot with this UUID -> closing channel
                snapshot = this.snapshots.get(message.data);
                snapshot.chanels.put(channel, true);
            } else { //There is no snapshot with this UUID -> creating snapshot and closing this channel
                snapshot = this.startSnapshot(message.data, channel);
            }

            if(this.checkCompletedSnapshot(snapshot)) { //snaphot completed -> create file
                try {
                    File f = new File(this.snapshotDir + File.separator + snapshot.uuid);
                    f.createNewFile();
                    FileWriter fw = new FileWriter(f);
                    fw.write("Balance: " + snapshot.balance + "\n");
                    fw.write(snapshot.listMessages());
                    fw.close();
                } catch (IOException e) {

                }
                snapshots.remove(snapshot);
            }
        } else { //record msg
            if(this.snapshots.isEmpty())
                return;
            for(var entry : snapshots.entrySet()) {
                if(!entry.getValue().chanels.get(channel))
                    entry.getValue().addMessage(message, channel);
            }
        }
    }

    public synchronized boolean anySnapshotInProgress() {
        return !snapshots.isEmpty();
    }

    public synchronized void addChannel(Channel channel) {
        this.channels.add(channel);
    }
}
