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
    boolean isFinish;
    int N_lines;
    int availableCredits;
    private final int delta = 5000;
    int nWorkers = 0;
    private final Object lock = new Object();
    DBMockup dbMockup= DBMockup.getInstance();

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
        this.isFinish=false;
    }

    public void associateUser(User user) {
        if(!isFinish){
            if (!associatedUsers.containsKey(user.getUserName())) {
                associatedUsers.put(user.getUserName(), user);
            }
        }else {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Não se aceitam mais utilizadores, Task Work terminada ");

        }
    }

    public boolean associateWorkers(ArrayList<WorkerRI> workersRI, User user) throws RemoteException {
        if(!isFinish){
            this.nWorkers= this.nWorkers + workersRI.size();
            if (!associatedWorkers.containsKey(user.getUserName())) {
                for (WorkerRI workerRI : workersRI) {
                    workerRI.setData(hashAlg, hashedCodes);
                }
                associatedWorkers.put(user.userName, workersRI);
                return true;
            }
        }else{
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Não se aceitam mais Workers, Task Work terminada ");
        }
        return false;
    }

    public Block getAvailableBlock() throws RemoteException {
        Block aux;
        synchronized (lock){
            if(!state || isFinish){
                return null;
            }
            if(!checkMoney()){
                this.state= false;
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Creditos Insuficientes! ");
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
                this.isFinish= true;
                this.state= false;
            }
            blocks.add(aux = new Block(false, true, blocks.size() * delta, end_of_block, isFinish));
            return aux;
        }

    }

    public boolean getstate() throws RemoteException {
        return state;
    }

    @Override
    public synchronized void discoveredHash(String hash, int index, WorkerRI worker) throws RemoteException, InterruptedException {
        this.availableCredits= availableCredits- 10;
        worker.addCredits(10);
        dbMockup.getUser(worker.getUser().userName).addCredits(10);
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
        if(!isFinish){
            if(user.getUserName().equals(this.owner.getUserName())){
                for (ArrayList<WorkerRI> workersRI : associatedWorkers.values()) {
                    for (WorkerRI workerRI : workersRI) {
                        workerRI.resumeThread();
                    }
                }
                this.state = true;
            }
        }else{
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Não é possivel contunar Task Work");
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
        this.availableCredits= availableCredits- auxCredits;
        work.addCredits(auxCredits);
        dbMockup.getUser(work.getUser().userName).addCredits(auxCredits);
        int aux = (int) ((block.endLine +1) / delta)-1;
        if(aux== -1){
            aux= 0;
        }
        blocks.get(aux).isFinished= true;
        blocks.get(aux).isOcupied= false;

        if(block.isLast){
            endAllThreads();
        }
    }

    @Override
    public boolean endTaskWork(User user) throws RemoteException {
        if(this.owner.getUserName().compareTo(user.getUserName()) == 0){
            endAllThreads();
            for (User user1: associatedUsers.values()){
                user1.associatedTaskGroups.remove(this);
            }
            this.owner.addCredits(availableCredits);
            this.availableCredits=0;
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Task Work apagada");
            return true;
        }else{
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Não tem permissões para apagar Task Work ");
            return false;
        }
    }

    @Override
    public String getName() throws RemoteException {
        return this.name;
    }

    private boolean checkMoney(){
        int cont = 1;
        for(Block block: blocks){
            if(block.isOcupied){
                cont++;
            }
        }
        return availableCredits >= cont * delta + (hashedCodes.size() * 10)*cont;
    }

    private void endAllThreads() throws RemoteException {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Encerrar todas as threads da Taskwork ");
        for (ArrayList<WorkerRI> workersRI : associatedWorkers.values()) {
            for (WorkerRI workerRI : workersRI) {
                workerRI.endThread();
            }
        }
    }

}
