package ProjetoSD.hashmatchproj.server;

import ProjetoSD.hashmatchproj.client.WorkerRI;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HashMatchSessionImpl extends UnicastRemoteObject implements HashMatchSessionRI {

    private DBMockup dataBase;
    private User user;
    private ArrayList<WorkerRI> createdWorkers = new ArrayList<>();

    public HashMatchSessionImpl(DBMockup dataBase, User user) throws RemoteException {
        super();
        this.dataBase = dataBase;
        this.user = user;
    }

    @Override
    public HashMatchTaskGroupRI createHashMatchTaskGroup(User user, String hashAlg, String filePath, ArrayList<String> hashCodes, String taskGroupName, int numberOfCredits, int N_line) throws RemoteException {
        HashMatchTaskGroupImpl hashMatchTaskGroupImpl;
        if(numberOfCredits > this.user.credits){
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Sem creditos suficientes!");
            return null;
        }
        if (!dataBase.taskGroups.containsKey(taskGroupName)) {
            dataBase.saveTaskGroup(taskGroupName, hashMatchTaskGroupImpl = new HashMatchTaskGroupImpl(user, filePath, hashAlg, hashCodes, taskGroupName, numberOfCredits,N_line));
            dataBase.taskGroups.get(taskGroupName).associateUser(this.user);
            this.user.associatedTaskGroups.add(dataBase.taskGroups.get(taskGroupName));
            remCredits(numberOfCredits);
            return hashMatchTaskGroupImpl;
        } else {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "TaskGroup already exists");
        }
        return null;
    }

    public ArrayList<String> getTaskGroupsName() throws RemoteException {
        ArrayList<String> taskGroupNames = new ArrayList<>();
        for (HashMatchTaskGroupImpl taskGroup : dataBase.taskGroups.values()) {
            taskGroupNames.add(taskGroup.name);
        }
        return taskGroupNames;
    }

    @Override
    public void joinTaskGroup(String taskGroupName) throws RemoteException {
        if (dataBase.taskGroups.containsKey(taskGroupName)) {
            if (!dataBase.taskGroups.get(taskGroupName).associatedUsers.containsKey(this.user.userName)) {
                dataBase.taskGroups.get(taskGroupName).associateUser(this.user);
                this.user.associatedTaskGroups.add(dataBase.taskGroups.get(taskGroupName));
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "User "+user.getUserName()+" associado com sucesso ao Task Group "+dataBase.taskGroups.get(taskGroupName).name);
            }
        }
    }

    @Override
    public HashMatchTaskGroupRI enterTaskGroupMenu(String taskGroupName) {
        for (HashMatchTaskGroupImpl taskGroup : user.associatedTaskGroups) {
            if (taskGroupName.equals(taskGroup.name)) {
                return taskGroup;
            }
        }
        return null;
    }

    @Override
    public void addCredits(int numberOfCredits) {

        this.user.credits += numberOfCredits;
    }

    public void remCredits(int numberOfCredits) {
        this.user.credits -= numberOfCredits;
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Creditos removidos: "+numberOfCredits+ " agora tem: "+this.user.credits);
    }

    public User getUser() {
        return this.user;
    }

}