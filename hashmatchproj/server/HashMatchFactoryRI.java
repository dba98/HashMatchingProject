package ProjetoSD.hashmatchproj.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface HashMatchFactoryRI extends Remote {

    HashMatchSessionRI login (String userName, String password) throws RemoteException;
    boolean register (String userName,String password) throws RemoteException;

}
