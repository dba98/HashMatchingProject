package ProjetoSD.hashmatchproj.client;

import ProjetoSD.hashmatchproj.models.Block;
import ProjetoSD.hashmatchproj.server.HashMatchTaskGroupRI;
import ProjetoSD.hashmatchproj.server.State;
import ProjetoSD.hashmatchproj.models.User;

import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Worker extends UnicastRemoteObject implements Runnable, WorkerRI {

    String filePath;
    public ArrayList<String> hashCodes;
    String encryptionFormat, threadName;
    HashMatchTaskGroupRI hashMatchTaskGroupRI;
    private Block block;
    Thread thread;
    State myState;
    boolean cycle;
    long i = 0;
    private User owner;
    int credits;
    private final Object lock = new Object();


    public Worker(HashMatchTaskGroupRI taskGroupRI, User owner) throws RemoteException {
        super();
        this.hashMatchTaskGroupRI = taskGroupRI;
        this.owner = owner;
        this.cycle = true;
        this.myState = taskGroupRI.getState();
    }

    @Override
    public void endThread() throws RemoteException {
        this.cycle = false;
        resumeThread();
    }


    public int encryptData(String input, String encryptionFormat) {
        try {
            int index = 0;
            // getInstance() method is called with algorithm SHA-512
            MessageDigest md = MessageDigest.getInstance(encryptionFormat);
            // digest() method is called
            // to calculate message digest of the input string
            // returned as array of byte
            byte[] messageDigest = md.digest(input.getBytes());
            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);
            // Convert message digest into hex value
            String hashtext = no.toString(16);
            // Add preceding 0s to make it 32 bit
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            //System.out.println(hashtext);
            for (String hashCode : hashCodes) {
                if (hashCode != null) {
                    if (hashCode.compareTo(hashtext) == 0) {
                        return index;
                    }
                }
                index++;
            }
            // return the HashText
            return -1;
        }
        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        // Implemtentar o algoritmo que vai tratar do ficheiro de texto.
        try {
            threadName = Thread.currentThread().getName();
            int index;
            URL url = new URL("https://raw.githubusercontent.com/danielmiessler/SecLists/master/Passwords/darkc0de.txt");

            synchronized (lock) {
                while (cycle) {
                    Scanner myReader = new Scanner(url.openStream());
                    this.block = hashMatchTaskGroupRI.getAvailableBlock();
                    if (block == null) {
                        if (checkState(myState, block))
                            continue;
                        else{
                            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Thread " + Thread.currentThread().getName() + "acabou! ganhou" + credits);
                            return;
                        }

                    }
                    i = 0;
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Searching Starting Line..." + block.startLine);
                    while (i < block.startLine) {
                        myReader.nextLine();
                        i++;
                    }
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Starting to encrypt from line: " + i + "   in Thread: " + Thread.currentThread().getName());
                    while (i <= block.endLine) {
                        if (checkState(myState, block)) {
                            String data = myReader.nextLine();
                            if ((index = encryptData(data, encryptionFormat)) > -1) {
                                System.out.println("******* ENCONTREI *********");
                                hashMatchTaskGroupRI.discoveredHash(data, index, this);
                            }
                            i++;
                        } else {
                            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Thread " + Thread.currentThread().getName() + "acabou! ganhou" + credits);
                            return;
                        }
                    }
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Ending on line" + (i - 1) + "   in Thread: " + Thread.currentThread().getName());
                    hashMatchTaskGroupRI.endBlock(block, this);
                    myReader.close();
                }
            }
        } catch (MalformedURLException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            block.startLine = i;
            try {
                hashMatchTaskGroupRI.saveBlock(block);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Thread " + Thread.currentThread().getName() + "acabou! ganhou" + credits);

    }

    private boolean checkState(State state, Block block) throws RemoteException, InterruptedException {
        switch (state.info) {
            case "PAUSE":
                synchronized (lock) {
                    this.lock.wait();
                }
                return true;
            case "DELETE":
                if (block != null){
                    hashMatchTaskGroupRI.endBlock(block, this);
                }
                return false;
            default:
                return true;
        }
    }


    @Override
    public void setData(String encryptionFormat, ArrayList<String> hashCodes) throws RemoteException {
        this.encryptionFormat = encryptionFormat;
        this.hashCodes = hashCodes;
    }

    @Override
    public void updateHashArray(ArrayList<String> hashCode) throws RemoteException {
        this.hashCodes = hashCode;
        System.out.println("MUDANÇA DO ARRAY NO THREAD" + threadName);
        for (String s : hashCodes) {
            System.out.println(s);
        }
    }

    @Override
    public void resumeThread() throws RemoteException {
        synchronized (lock) {
            myState = this.hashMatchTaskGroupRI.getState();
            lock.notify();
        }
    }

    @Override
    public void addCredits(int newCredits) throws RemoteException {
        this.credits = this.credits + newCredits;
    }

    @Override
    public User getUser() throws RemoteException {
        return owner;
    }

    @Override
    public void update() throws RemoteException {
        this.myState = hashMatchTaskGroupRI.getState();
    }

}
