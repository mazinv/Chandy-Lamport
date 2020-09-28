package cz.zcu.kiv.ds.mazin.snapshot;

import cz.zcu.kiv.ds.mazin.messaging.Channel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SnapshotService {
    private Map<String, Snapshot> snapshots;
    private String snapshotDir;
    private Set<Channel> channels;

    public SnapshotService(String snapshotDir) {
        this.snapshotDir = snapshotDir;
        this.snapshots = new HashMap<>();
        this.channels = new HashSet<>();
    }

    public void addChannel(Channel channel) {
        this.channels.add(channel);
    }
}
