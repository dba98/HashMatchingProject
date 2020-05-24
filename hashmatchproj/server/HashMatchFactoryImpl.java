package ProjetoSD.hashmatchproj.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class HashMatchFactoryImpl extends UnicastRemoteObject implements HashMatchFactoryRI {

    DBMockup dataBase = new DBMockup();
    HashMap<String, HashMatchSessionRI> sessions = new HashMap<>();
    HashMap<String, HashMatchTaskGroupRI> taskGroups = new HashMap<>();

    public HashMatchFactoryImpl() throws RemoteException {
        super();
    }

    @Override
    public HashMatchSessionRI login(String userName, String password) throws RemoteException {
        if (dataBase.exists(userName, password)) {
            if (!sessions.containsKey(userName)) {
                sessions.put(userName, new HashMatchSessionImpl(dataBase,dataBase.getUser(userName, password)));
            }
            return sessions.get(userName);
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
