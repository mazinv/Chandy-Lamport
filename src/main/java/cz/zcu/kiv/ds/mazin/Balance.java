package cz.zcu.kiv.ds.mazin;

public class Balance {

    private long balance;

    public Balance() {
        this.balance = 100L;
    }

    public synchronized long add(long amount) {
        this.balance += amount;
        return this.balance;
    }

    public synchronized long sub(long amount) {
        this.balance -= amount;
        return this.balance;
    }

    public synchronized long getBalance() {
        return this.balance;
    }
}
