package ProjetoSD.hashmatchproj.server;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface HashMatchSessionRI extends Remote {
    HashMatchTaskGroupRI createHashMatchTaskGroup(User user, String hashAlg, String filePath, ArrayList<String> hashedCodes) throws RemoteException;

    User getUser(String userName, String password);

    void listTaskGroups();

    void joinTaskGroup(String taskGroupName);

    boolean enterTaskGroupMenu(String taskGroupName);
}
