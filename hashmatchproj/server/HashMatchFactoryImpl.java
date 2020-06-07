package ProjetoSD.hashmatchproj.server;

import ProjetoSD.hashmatchproj.models.User;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class HashMatchFactoryImpl extends UnicastRemoteObject implements HashMatchFactoryRI {

    DBMockup dataBase = DBMockup.getInstance();
    HashMap<String, HashMatchSessionImpl> sessions = new HashMap<>();
    // HashMap<String, HashMatchTaskGroupRI> taskGroups = new HashMap<>();

    public HashMatchFactoryImpl() throws RemoteException {
        super();
    }

    @Override
    public HashMatchSessionRI login(String userName, String password) throws RemoteException {
        HashMatchSessionImpl sessionRI;
        if (dataBase.exists(userName, password)) {
            if (!sessions.containsKey(userName)) {
                sessions.put(userName, sessionRI = new HashMatchSessionImpl(dataBase,dataBase.getUser(userName, password)));
                this.dataBase.sessions.put(userName,sessionRI);
                return sessions.get(userName);
            }
        }
        return null;
    }

    @Override
    public boolean register(String userName, String password) throws RemoteException {
       if(!dataBase.exists(userName,password)){
           dataBase.users.add(new User(userName, password));
           return true;
       }
       return false;
    }
}
