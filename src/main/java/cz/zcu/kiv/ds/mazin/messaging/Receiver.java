package cz.zcu.kiv.ds.mazin.messaging;

import cz.zcu.kiv.ds.mazin.Balance;
import cz.zcu.kiv.ds.mazin.snapshot.SnapshotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Receiver implements Runnable {
    private Channel channel;
    private Balance balance;
    private SnapshotService snapshotService;
    private Logger logger = LoggerFactory.getLogger(Receiver.class);

    public Receiver(Channel channel, Balance balance, SnapshotService snapshotService) {
        this.channel = channel;
        this.balance = balance;
        this.snapshotService = snapshotService;
    }

    @Override
    public void run() {
        while (true) {
            var message = channel.receive();
            switch (message.type) {
                case CREDIT:
                    this.balance.add(Long.parseLong(message.data));
                break;
                case DEBIT:
                    this.balance.sub(Long.parseLong(message.data));
                break;
                case MARKER:
                    try {
                        Thread.sleep(15 * 1000);
                    } catch (InterruptedException e) {
                        logger.error("Receiver sleep exception");
                        System.exit(-1);
                    }
                    break;
                default:
                    break;
            }

            this.snapshotService.recordMessage(message, this.channel);
        }
    }
}
