package ProjetoSD.hashmatchproj.client;

import ProjetoSD.hashmatchproj.server.Block;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface WorkerRI extends Remote {

    void setData(String encryptionFormat, ArrayList<String> hashCodes, Block block) throws RemoteException;
    void setStopThread() throws RemoteException, InterruptedException;
    void updateHashArray(ArrayList<String> hashCodes) throws RemoteException;
    public void setStartThread() throws RemoteException;
}
