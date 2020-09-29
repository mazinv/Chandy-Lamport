package cz.zcu.kiv.ds.mazin.messaging;

import cz.zcu.kiv.ds.mazin.Balance;
import cz.zcu.kiv.ds.mazin.snapshot.SnapshotService;

import java.util.Random;

public class Sender implements Runnable {
    private Channel channel;
    private Random random;
    private Balance balance;
    private SnapshotService snapshotService;

    public Sender(Channel channel, Balance balance, SnapshotService snapshotService) {
        this.channel = channel;
        this.random = new Random();
        this.balance = balance;
        this.snapshotService = snapshotService;
    }

    @Override
    public void run() {
        while (true) {
            long amount = 1 + (long) (random.nextDouble() * (10 - 1));
            Message message;
            if(random.nextBoolean()) { //sending credit
                message = new Message(MessageType.CREDIT, Long.toString(amount));
                balance.sub(amount);
            } else { //sending debit
                message = new Message(MessageType.DEBIT, Long.toString(amount));
                balance.add(amount);
            }
            this.channel.send(message);
            try {
                Thread.sleep((amount * 1000) / 2);
            } catch (InterruptedException e) {

            }
        }
    }
}
