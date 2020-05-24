package ProjetoSD.hashmatchproj.server;

import ProjetoSD.hashmatchproj.client.Worker;

import java.io.File;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public class HashMatchSessionImpl implements HashMatchSessionRI, Serializable {

    DBMockup dataBase;
    User user;
    ArrayList<Worker> createdWorkers = new ArrayList<>();

    HashMap<String,HashMatchTaskGroupRI> taskGroups = new HashMap<>();
    public HashMatchSessionImpl(DBMockup dataBase,User user) {
       this.dataBase = dataBase;
       this.user = user;
    }

    @Override
    public HashMatchTaskGroupRI createHashMatchTaskGroup(User user, String hashAlg, File file,ArrayList<String> hashCodes) throws RemoteException {
        String aux;
        HashMatchTaskGroupRI hashMatchTaskGroupRI;
        if(!taskGroups.containsKey(aux = user.getUserName().concat("_").concat(file.getName()))){
            taskGroups.put(aux,hashMatchTaskGroupRI = new HashMatchTaskGroupImpl(user,file,hashAlg,hashCodes));
            return hashMatchTaskGroupRI;
        }
        return null;
    }
    public void listTaskGroups(){
        taskGroups.forEach((key, value) -> System.out.println(key));
    }

    public User getUser(String userName,String password){
        return this.dataBase.getUser(userName,password);
    }

}