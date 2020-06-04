package ProjetoSD.hashmatchproj.client;

import ProjetoSD.hashmatchproj.server.Block;
import ProjetoSD.hashmatchproj.server.HashMatchTaskGroupRI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
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

    public boolean doStop = false;
    String filePath;
    public ArrayList<String> hashCodes;
    String encryptionFormat;
    HashMatchTaskGroupRI hashMatchTaskGroupRI;
    Block block;
    Thread thread;
    boolean state;
    boolean cycle;
    long i = 0;

    public Worker(HashMatchTaskGroupRI taskGroupRI) throws RemoteException {
        super();
        this.hashMatchTaskGroupRI = taskGroupRI;
        this.state = true;
        this.cycle= true;
    }

    public void endThread() {
        this.cycle = false;

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
            int index;
            URL url = new URL("https://raw.githubusercontent.com/danielmiessler/SecLists/master/Passwords/darkc0de.txt");
            synchronized (this) {
                while (cycle) {
                    if(!this.state){
                        this.wait();
                    }
                    Scanner myReader = new Scanner(url.openStream());
                    block = hashMatchTaskGroupRI.getAvailableBlock();
                    if (block == null) {
                        break;
                    }
                    i = 0;
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Searching Starting Line..." + block.startLine);
                    while (i < block.startLine) {
                        myReader.nextLine();
                        i++;
                    }
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Starting to encrypt from line " + i + "...");
                    while (i < block.endLine) {
                        String data = myReader.nextLine();
                        if ((index = encryptData(data, encryptionFormat)) > -1) {
                            System.out.println("******* ENCONTEI *********");
                            hashMatchTaskGroupRI.discoveredHash(data, index);
                        }
                        i++;
                    }

                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Ending on line" + i + "...");
                    myReader.close();
                }
            }
            } catch(MalformedURLException e){
                System.out.println("An error occurred.");
                e.printStackTrace();
            } catch(IOException | InterruptedException e){
                e.printStackTrace();
                block.startLine= i;
            try {
                hashMatchTaskGroupRI.saveBlock(block);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }


    }


    @Override
    public void setData(String encryptionFormat, ArrayList<String> hashCodes) throws RemoteException {
        this.encryptionFormat = encryptionFormat;
        this.hashCodes = hashCodes;
    }

    @Override
    public synchronized void setStopThread() throws RemoteException, InterruptedException {
        this.thread.wait();
    }

    public synchronized void setStartThread() throws RemoteException{
       this.thread.notify();
    }


    @Override
    public void updateHashArray(ArrayList<String> hashCode) throws RemoteException {
        this.hashCodes =hashCode;
        System.out.println( "MUDANÇA NO ARRAY");
        for(String s: hashCodes){
            System.out.println( s);
        }
    }

    @Override
    public void resumeThread() throws RemoteException {
        synchronized (this) {
            this.state = true;
            this.notify();
        }
    }

    @Override
    public void stopThread() throws RemoteException {
        this.state = false;
    }


}
