package ProjetoSD.hashmatchproj.client;

import ProjetoSD.hashmatchproj.server.HashMatchTaskGroupRI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;

public class Worker implements Runnable,WorkerRI {

    public boolean doStop = false;
    public long startLine,delta;
    File file;
    public ArrayList<String> hashCodes;
    String encryptionFormat;
    HashMatchTaskGroupRI hashMatchTaskGroupRI;

    public Worker() {

    }

    public synchronized void doStop() {
        this.doStop = true;
    }

    public synchronized boolean keepRunnning() {
        return !this.doStop;
    }


    public boolean encryptData(String input,String encryptionFormat){
        try {
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
            System.out.println(hashtext);
            for (String hashCode : hashCodes){
                if(hashCode.compareTo(hashtext)==0){
                    return true;
                }
            }
            // return the HashText
            return false;
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
                URL url = new URL("https://raw.githubusercontent.com/danielmiessler/SecLists/master/Passwords/darkc0de.txt");
                Scanner myReader = new Scanner(url.openStream());
                long i = startLine;
                while(i < delta){
                    String data = myReader.nextLine();
                    if(encryptData(data,encryptionFormat)){
                        System.out.println("Discovered Hash");
                        break;
                    }
                    i++;
                }
                myReader.close();
            } catch (MalformedURLException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }


    @Override
    public void setData(long startLine,long delta,String encryptionFormat,ArrayList<String> hashCodes) {
        this.startLine = startLine;
        this.delta = delta;
        this.encryptionFormat = encryptionFormat;
        this.hashCodes = hashCodes;

    }

    @Override
    public void setStopThread(boolean doStop) {
        this.doStop = doStop;
    }
}
