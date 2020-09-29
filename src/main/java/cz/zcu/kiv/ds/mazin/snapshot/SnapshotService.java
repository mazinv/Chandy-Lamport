package cz.zcu.kiv.ds.mazin.snapshot;

import cz.zcu.kiv.ds.mazin.messaging.Channel;
import cz.zcu.kiv.ds.mazin.messaging.Message;
import cz.zcu.kiv.ds.mazin.messaging.MessageType;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SnapshotService {
    private Map<String, Snapshot> snapshots;
    private String snapshotDir;
    private Set<Channel> channels;

    public SnapshotService(String snapshotDir) {
        this.snapshotDir = snapshotDir;
        this.snapshots = new HashMap<>();
        this.channels = new HashSet<>();
    }

    public synchronized Snapshot startSnapshot(String uuid, Channel channel) {
        Map<Channel, Boolean> channels = new HashMap<>();
        this.channels.forEach(c -> {
            if(c == channel)
                channels.put(c, true); //empty channel - if marker came from some channel
            else
                channels.put(c, false); //not empty channel - if marker was created by this process
        });
        var snapshot = new Snapshot(uuid, channels);
        snapshots.put(uuid, snapshot);

        sendMarkers(channels, uuid);

        return snapshot;
    }

    private void sendMarkers(Map<Channel, Boolean> channels, String uuid) {
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

    private synchronized void checkSnapshotsCompleteness(Snapshot snapshot) {
        if(this.checkCompletedSnapshot(snapshot)) {
            //TODO print snapshot into file
            try {
                File f = new File(this.snapshotDir + File.pathSeparator + snapshot.uuid);
                f.createNewFile();
            } catch (IOException e) {

            }
            snapshots.remove(snapshot);
        }
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

            if(this.checkCompletedSnapshot(snapshot)) {
                //TODO print snapshot into file
                try {
                    File f = new File(this.snapshotDir + File.pathSeparator + snapshot.uuid);
                    f.createNewFile();
                } catch (IOException e) {

                }
                snapshots.remove(snapshot);
            }
        } else {
            //TODO record messages
            if(this.snapshots.isEmpty())
                return;
        }
    }

    public synchronized boolean anySnapshotInProgress() {
        return !snapshots.isEmpty();
    }

    public void addChannel(Channel channel) {
        this.channels.add(channel);
    }
}
