package ProjetoSD.hashmatchproj.client;

import java.io.File;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Worker implements Runnable,WorkerRI {

    public boolean doStop = false;
    public long startLine,delta;
    File file;
    ArrayList<String> hashCodes;
    String encryptionFormat;

    public Worker(long startLine, long delta, File file, ArrayList<String> hashCodes, String encryptionFormat){
        this.startLine = startLine;
        this.delta = delta;
        this.file = file;
        this.hashCodes = hashCodes;
        this.encryptionFormat = encryptionFormat;
    }

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
            for (String hashCode : hashCodes){
                if(hashCode.equals(hashtext)){
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
        while (keepRunnning()) {
            // Implemtentar o algoritmo que vai tratar do ficheiro de texto.
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
