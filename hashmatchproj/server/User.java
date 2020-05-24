package ProjetoSD.hashmatchproj.server;

import java.io.Serializable;
import java.util.HashMap;

public class User implements Serializable {

    String userName;
    String password;
    HashMap<String,Thread> userWorkers = new HashMap<>();

    public User(String userName, String password){
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public HashMap<String, Thread> getUserWorkers() {
        return userWorkers;
    }
}
