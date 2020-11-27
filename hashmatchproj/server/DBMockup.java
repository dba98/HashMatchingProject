package ProjetoSD.hashmatchproj.server;

import ProjetoSD.hashmatchproj.models.User;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBMockup {

    private static DBMockup single_instance = null;

    ArrayList<User> users = new ArrayList<>();
    ArrayList<File> files = new ArrayList<>();
    HashMap<String, HashMatchTaskGroupImpl> taskGroups = new HashMap<>();
    HashMap<String, HashMatchSessionImpl> sessions = new HashMap<>();

    private DBMockup() {
        users.add(new User("Diogo", encryptData("12345","SHA-512")));
        users.add(new User("Ricardo", encryptData("12345","SHA-512")));
        users.add(new User("Hugo", encryptData("12345","SHA-512")));
        users.get(0).addCredits(10000000);
        users.get(1).addCredits(50000);
        users.get(2).addCredits(100000);

    }

    public synchronized static DBMockup getInstance() {
        if (single_instance == null)
            single_instance = new DBMockup();
        return single_instance;
    }

    public boolean exists(String userName, String password) {
        for (User user : this.users) {
            if (user.getUserName().compareTo(userName) == 0 && user.getPassword().compareTo(encryptData(password,"SHA-512")) == 0) {
                return true;
            }
        }
        return false;
    }

    public void register(String userName, String password) {
        if (!exists(userName, password)) {
            users.add(new User(userName, encryptData(password,"SHA-512")));
        }
    }

    public void saveTaskGroup(String uniqueName, HashMatchTaskGroupImpl taskGroup) {
        this.taskGroups.put(uniqueName, taskGroup);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "TaskGroup "+uniqueName+" saved successfully");
    }

    public User getUser(String userName, String password) {
        for (User user : this.users) {
            if (user.getUserName().compareTo(userName) == 0 && user.getPassword().compareTo(encryptData(password,"SHA-512")) == 0) {
                return user;
            }
        }
        return null;
    }

    public User getUser(String userName) {
        for (User user : this.users) {
            if (user.getUserName().compareTo(userName) == 0 ) {
                return user;
            }
        }
        return null;
    }
    public String encryptData(String input, String encryptionFormat) {
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
            // return the HashText
            return hashtext;
        }
        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
