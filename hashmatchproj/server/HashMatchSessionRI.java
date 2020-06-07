package ProjetoSD.hashmatchproj.server;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface HashMatchSessionRI extends Remote {
    HashMatchTaskGroupRI createHashMatchTaskGroup(User user, String hashAlg, String filePath, ArrayList<String> hashedCodes,String taskGroupName,int numberOfCredits, int N_line) throws RemoteException;

    User getUser() throws RemoteException;

    void joinTaskGroup(String taskGroupName) throws RemoteException;

    HashMatchTaskGroupRI enterTaskGroupMenu(String taskGroupName) throws RemoteException;

    void addCredits(int numberOfCredits) throws RemoteException;

    boolean endTaskWork(User user, String taskGroupName) throws RemoteException;


    ArrayList<String> getTaskGroupsName() throws RemoteException;
}
