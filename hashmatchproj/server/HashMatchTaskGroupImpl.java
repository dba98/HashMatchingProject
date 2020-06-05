package ProjetoSD.hashmatchproj.server;

import ProjetoSD.hashmatchproj.client.WorkerRI;

import java.lang.reflect.Array;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HashMatchTaskGroupImpl extends UnicastRemoteObject implements HashMatchTaskGroupRI {

    User owner;
    HashMap<String, User> associatedUsers = new HashMap<>();
    HashMap<String, ArrayList<WorkerRI>> associatedWorkers = new HashMap<>();
    ArrayList<Block> blocks = new ArrayList<>();
    ArrayList<String> hashedCodes;
    String workingFile, hashAlg, name;
    boolean state;
    int N_lines;
    int availableCredits;
    private final int delta = 5000;
    private final Object lock = new Object();


    public HashMatchTaskGroupImpl(User owner, String file, String hashAlg, ArrayList<String> hashedCodes, String name, int numberOfCredits, int N_lines) throws RemoteException {
        super();
        this.owner = owner;
        this.workingFile = file;
        this.hashAlg = hashAlg;
        this.hashedCodes = hashedCodes;
        this.name = name;
        this.availableCredits = numberOfCredits;
        this.N_lines= N_lines;
        this.state = true;
    }

    public void associateUser(User user) {
        if (!associatedUsers.containsKey(user.getUserName())) {
            associatedUsers.put(user.getUserName(), user);
        }
    }

    public void associateWorkers(ArrayList<WorkerRI> workersRI, User user) throws RemoteException {
        if (!associatedWorkers.containsKey(user.getUserName())) {
            for (WorkerRI workerRI : workersRI) {
                workerRI.setData(hashAlg, hashedCodes);
            }
            associatedWorkers.put(user.userName, workersRI);
        }
    }

    public Block getAvailableBlock() throws RemoteException {
        Block aux;
        synchronized (lock){
            if(!state){
                return null;
            }
            int  end_of_block=((blocks.size() + 1) * delta) - 1;

            if (blocks.size() > 0) {
                for (Block block : blocks) {
                    if (!block.isFinished && !block.isOcupied) {
                        return block;
                    }
                }
            }
            if(end_of_block> N_lines){
                end_of_block= N_lines;
                this.state= false;
            }
            blocks.add(aux = new Block(false, true, blocks.size() * delta, end_of_block));
            return aux;
        }

    }

    public boolean getstate() throws RemoteException {
        return state;
    }

    @Override
    public synchronized void discoveredHash(String hash, int index, WorkerRI worker) throws RemoteException, InterruptedException {
        worker.addCredits(10);
        hashedCodes.set(index, null);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Hash of word "+hash+" discovered!");
        for (ArrayList<WorkerRI> workersRI : associatedWorkers.values()) {
            for (WorkerRI workerRI : workersRI) {
              workerRI.updateHashArray(hashedCodes);
            }
        }
    }

    public ArrayList<String> getHashedCodes() {
        return hashedCodes;
    }

    @Override
    public User getOwner() throws RemoteException {
        return this.owner;
    }

    public void stopTaskWork(User user) throws RemoteException, InterruptedException {

        if(user.getUserName().equals(this.owner.getUserName())){
            for (ArrayList<WorkerRI> workersRI : associatedWorkers.values()) {
                for (WorkerRI workerRI : workersRI) {
                    workerRI.stopThread();
                }
            }
            this.state = false;
        }
    }

    public void resumeTaskWork(User user) throws RemoteException{
        if(user.getUserName().equals(this.owner.getUserName())){
            for (ArrayList<WorkerRI> workersRI : associatedWorkers.values()) {
                for (WorkerRI workerRI : workersRI) {
                    workerRI.resumeThread();
                }
            }
            this.state = true;
        }
    }

    @Override
    public void clearMyWorks(User user) throws RemoteException {
        associatedWorkers.remove(user.userName);
    }

    @Override
    public void saveBlock(Block block) throws RemoteException {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Block save in line: "+block.startLine);
        int aux = (int) ((block.endLine +1) / delta)-1;
        blocks.get(aux).startLine = block.startLine;
        blocks.get(aux).isOcupied= false;

    }

    @Override
    public void endBlock(Block block, WorkerRI work) throws RemoteException {
        int auxCredits= (int) ((block.endLine+1)-block.startLine);
        work.addCredits(auxCredits);
        int aux = (int) ((block.endLine +1) / delta)-1;
        blocks.get(aux).isFinished= true;
    }


}
