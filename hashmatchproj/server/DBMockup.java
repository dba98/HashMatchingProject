package ProjetoSD.hashmatchproj.server;

import java.io.File;
import java.io.Serializable;
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
        users.add(new User("Diogo", "12345"));
        users.add(new User("Ricardo", "12345"));
    }

    public synchronized static DBMockup getInstance() {
        if (single_instance == null)
            single_instance = new DBMockup();
        return single_instance;
    }

    public boolean exists(String userName, String password) {
        for (User user : this.users) {
            if (user.getUserName().compareTo(userName) == 0 && user.getPassword().compareTo(password) == 0) {
                return true;
            }
        }
        return false;
    }

    public void register(String userName, String password) {
        if (!exists(userName, password)) {
            users.add(new User(userName, password));
        }
    }

    public void saveTaskGroup(String uniqueName, HashMatchTaskGroupImpl taskGroup) {
        this.taskGroups.put(uniqueName, taskGroup);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "TaskGroup "+uniqueName+" saved successfully");
    }

    public User getUser(String userName, String password) {
        for (User user : this.users) {
            if (user.getUserName().compareTo(userName) == 0 && user.getPassword().compareTo(password) == 0) {
                return user;
            }
        }
        return null;
    }

}
