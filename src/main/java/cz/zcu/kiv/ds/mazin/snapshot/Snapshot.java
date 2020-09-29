package cz.zcu.kiv.ds.mazin.snapshot;

import cz.zcu.kiv.ds.mazin.messaging.Channel;

import java.util.Map;

public class Snapshot {
    public final String uuid;
    public final Map<Channel, Boolean> chanels;

    public Snapshot(String uuid, Map<Channel, Boolean> channels) {
        this.uuid = uuid;
        this.chanels = channels;
    }
}
