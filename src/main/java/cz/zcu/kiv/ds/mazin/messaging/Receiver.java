package cz.zcu.kiv.ds.mazin.messaging;

import cz.zcu.kiv.ds.mazin.Balance;

public class Receiver implements Runnable {
    private Channel channel;
    private Balance balance;

    public Receiver(Channel channel, Balance balance) {
        this.channel = channel;
        this.balance = balance;
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
                    break;
                default:
                    break;
            }
        }
    }
}
