package ProjetoSD.hashmatchproj.server;

import ProjetoSD.hashmatchproj.client.Worker;

import java.io.File;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HashMatchSessionImpl implements HashMatchSessionRI, Serializable {

    private DBMockup dataBase;
    private User user;
    private ArrayList<Worker> createdWorkers = new ArrayList<>();
    HashMap<String, HashMatchTaskGroupImpl> taskGroups = new HashMap<>();

    public HashMatchSessionImpl(DBMockup dataBase, User user) {
        this.dataBase = dataBase;
        this.user = user;
    }

    @Override
    public HashMatchTaskGroupRI createHashMatchTaskGroup(User user, String hashAlg, String filePath, ArrayList<String> hashCodes) throws RemoteException {
        String aux;
        HashMatchTaskGroupImpl hashMatchTaskGroupImpl;

        if (!taskGroups.containsKey(aux = user.getUserName().concat("_").concat(filePath))) {
            taskGroups.put(aux, hashMatchTaskGroupImpl = new HashMatchTaskGroupImpl(user, filePath, hashAlg, hashCodes));
            return hashMatchTaskGroupImpl;
        }else{
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "TaskGroup already exists");
        }
        return null;
    }

    public void listTaskGroups() {
        taskGroups.forEach((key, value) -> System.out.println(key));
    }

    @Override
    public void joinTaskGroup(String taskGroupName) {
        if (taskGroups.containsKey(taskGroupName)) {
            if (!taskGroups.get(taskGroupName).owner.userName.equals(this.user.userName)) {
                taskGroups.get(taskGroupName).associateUser(this.user);
                this.user.associatedTaskGroup.add(taskGroups.get(taskGroupName));
            }
        }
    }

    @Override
    public boolean enterTaskGroupMenu(String taskGroupName) {
        return taskGroups.containsKey(taskGroupName);
    }

    public User getUser(String userName, String password) {
        return this.dataBase.getUser(userName, password);
    }

}