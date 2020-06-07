package ProjetoSD.hashmatchproj.server;

import ProjetoSD.hashmatchproj.client.WorkerRI;
import ProjetoSD.hashmatchproj.models.Block;
import ProjetoSD.hashmatchproj.models.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface HashMatchTaskGroupRI extends Remote {

     void associateUser(User user) throws RemoteException;
     boolean associateWorkers(ArrayList<WorkerRI> workersRI, User user) throws RemoteException;
     User getOwner() throws RemoteException;
     void discoveredHash(String hash, int index, WorkerRI worker) throws RemoteException, InterruptedException;
     ArrayList<String> getHashedCodes() throws RemoteException;
     State getState() throws RemoteException;
     Block getAvailableBlock() throws RemoteException;
     void stopTaskWork(User user) throws RemoteException, InterruptedException;
     void resumeTaskWork(User user) throws RemoteException;
     void clearMyWorks(User user) throws RemoteException;
     void saveBlock(Block block) throws RemoteException;
     void endBlock(Block block, WorkerRI work) throws RemoteException;
     String getName() throws RemoteException;
     ArrayList<String> getWordsFound(User user) throws RemoteException;

}
