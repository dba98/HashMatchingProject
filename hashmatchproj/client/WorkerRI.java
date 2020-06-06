package ProjetoSD.hashmatchproj.client;

import ProjetoSD.hashmatchproj.server.Block;
import ProjetoSD.hashmatchproj.server.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface WorkerRI extends Remote {

    void endThread() throws RemoteException;

    void setData(String encryptionFormat, ArrayList<String> hashCodes) throws RemoteException;
   void updateHashArray(ArrayList<String> hashCode) throws RemoteException;
   void resumeThread() throws RemoteException;
   void addCredits(int newCredits)  throws RemoteException;
   User getUser() throws RemoteException;
    void update() throws RemoteException;

}
