package ProjetoSD.hashmatchproj.server;

import ProjetoSD.hashmatchproj.client.WorkerRI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface HashMatchTaskGroupRI extends Remote {

    public void associateUser(User user) throws RemoteException;
    public void associateWorkers(ArrayList<WorkerRI> workersRI, User user) throws RemoteException;
    public User getOwner() throws RemoteException;
    public void discoveredHash(String hash, int index) throws RemoteException, InterruptedException;
    public ArrayList<String> getHashedCodes() throws RemoteException;
    public boolean getstate() throws RemoteException;
    public Block getAvailableBlock() throws RemoteException;
}
