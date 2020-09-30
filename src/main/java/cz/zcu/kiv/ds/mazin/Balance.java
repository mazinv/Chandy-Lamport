package cz.zcu.kiv.ds.mazin;

public class Balance {

    private long balance;
    private final Object lock = new Object();

    public Balance() {
        this.balance = 100L;
    }

    public long add(long amount) {
        synchronized (lock) {
            this.balance += amount;
            return this.balance;
        }
    }

    public long sub(long amount) {
        synchronized (lock) {
            this.balance -= amount;
            return this.balance;
        }
    }

    public long getBalance() {
        synchronized (lock) {
            return this.balance;
        }
    }
}
