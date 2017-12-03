import java.util.ArrayList;
import java.util.concurrent.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Bank {
    BlockingQueue<Transaction> queue;
    private final Transaction nullTrans = new Transaction(-1,0,0);

    Account[] accounts = new Account[20];

    public Bank(){
        queue = new LinkedBlockingQueue<>();
        for(int i = 0; i < accounts.length; i++){
            accounts[i] = new Account(i);
        }
    }
    // TODO: Add code for actually updating accounts. 
    // Should you make this synchronized? Why or why not?
    // If not, how do you avoid concurrency issues?
    public synchronized void processTransaction(Transaction t) {
        int amount = t.amount;

        Account from = accounts[t.fromAccount];
        from.setNumOfTran(from.getNumOfTran() + 1);
        from.setBalance(from.getBalance() - amount);

        Account to = accounts[t.toAccount];
        to.setNumOfTran(to.getNumOfTran() + 1);
        to.setBalance(to.getBalance() + amount);
    }

    private class Worker extends Thread {
        public void run() {
            try {
                Transaction t;
                do {
                    t = queue.take();
                    if(t.fromAccount != -1){
                        processTransaction(t);
                        //System.out.println(this.getName() + t);
                    }
                } while (t.fromAccount != -1);
            } catch (InterruptedException e) {
                System.out.println("interrupted");
            }
            System.out.println(this.getName()  + "exiting");
        }
    }

    public static void main(String[] args) throws IOException{

        File fi = new File(args[0]);
        int num = Integer.parseInt(args[1]);


        Bank bank = new Bank();

        // TODO: Replace the following with code to generate as
        // many workers as needed. The number of workers is
        // Given as a commandline argument.
        ArrayList<Worker> workers = new ArrayList<>();
        for (int i = 0; i < num; i++){
            workers.add(bank.new Worker());
        }

        try {

            // TODO: replace the following with code for 
            // reading from the file and putting the transactions 
            // into the BlockingQueue

            for(int i = 0; i < num; i ++){
                workers.get(i).start();
            }

            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fi))){
                String string;
                while((string = bufferedReader.readLine())!= null){
                    String[] strings = string.split(" ");
                    bank.queue.put(new Transaction(Integer.parseInt(strings[0]),Integer.parseInt(strings[1]),Integer.parseInt(strings[2])));
                }
            }

            for(int i = 0; i < num; i++){
                bank.queue.put(bank.nullTrans);
            }

            System.out.println("Main finished adding all transactions");

            // TODO: Add code here to wait for ALL the workers to finish
            for(int i = 0; i < num; i++){
                workers.get(i).join();
            }



        } catch (InterruptedException e) {
            System.out.println("interrupted");
        }
        System.out.println("All threads done");

        for(int i = 0; i < bank.accounts.length; i++){
            System.out.println(bank.accounts[i].toString());
        }

    }
}

class Transaction {
    int fromAccount;
    int toAccount;
    int amount;

    public Transaction(int from, int to, int amt) {
        fromAccount = from;
        toAccount = to;
        amount = amt;
    }

//    public String toString() {
//        return "Transaction: from = " + fromAccount + ", to = " + toAccount + " amount = " + amount;
//    }

}

//TODO: Create an Account class with id, num of transactions and account balance

class Account{
    private int id;
    private int numOfTran;
    private int balance;

    public Account(int id) {
        setId(id);
        setBalance(1000);
        setNumOfTran(0);
    }


    public int getBalance(){
        return balance;
    }
    public int getID(){
        return id;
    }
    public int getNumOfTran(){
        return numOfTran;
    }

    public void setId(int id){
        this.id = id;
    }
    public void setNumOfTran(int nt){
        this.numOfTran = nt;
    }
    public void setBalance(int b){
        this.balance = b;
    }
    @Override
    public String toString(){
        return "acct: " + getID() + " bal: "+ getBalance() + " trans: " + getNumOfTran() ;
    }
}