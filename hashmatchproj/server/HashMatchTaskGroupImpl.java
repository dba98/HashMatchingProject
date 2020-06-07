package ProjetoSD.hashmatchproj.server;
import ProjetoSD.hashmatchproj.client.WorkerRI;
import ProjetoSD.hashmatchproj.models.Block;
import ProjetoSD.hashmatchproj.models.User;

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
    State taskGroupState;
    boolean isFinished;
    int N_lines;
    int availableCredits;
    private final int delta = 5000;
    int nWorkers = 0;
    ArrayList<String> hashedCodesFound = new ArrayList<>();
    private final Object lock = new Object();
    DBMockup dbMockup = DBMockup.getInstance();

    public HashMatchTaskGroupImpl(User owner, String file, String hashAlg, ArrayList<String> hashedCodes, String name, int numberOfCredits, int N_lines) throws RemoteException {
        super();
        this.owner = owner;
        this.workingFile = file;
        this.hashAlg = hashAlg;
        this.hashedCodes = hashedCodes;
        this.name = name;
        this.availableCredits = numberOfCredits;
        this.N_lines = N_lines;
        this.isFinished = false;
        this.taskGroupState = new State("DOWORK");
    }

    public void associateUser(User user) {
        if (!isFinished) {
            if (!associatedUsers.containsKey(user.getUserName())) {
                associatedUsers.put(user.getUserName(), user);
            }
        } else {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Não se aceitam mais utilizadores, Task Work terminada ");

        }
    }

    public boolean associateWorkers(ArrayList<WorkerRI> workersRI, User user) throws RemoteException {
        if (!isFinished) {
            this.nWorkers = this.nWorkers + workersRI.size();
            if (!associatedWorkers.containsKey(user.getUserName())) {
                for (WorkerRI workerRI : workersRI) {
                    workerRI.setData(hashAlg, hashedCodes);
                }
                associatedWorkers.put(user.getUserName(), workersRI);
                return true;
            }
        } else {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Não se aceitam mais Workers, Task Work terminada ");
        }
        return false;
    }

    public Block getAvailableBlock() throws RemoteException {
        Block aux;
        synchronized (lock) {
            int end_of_block = ((blocks.size() + 1) * delta) - 1;
            if (end_of_block > N_lines) {
                this.taskGroupState.info = "DELETE";
                notifyAllWorkers();
                return null;
            }
            if (!checkMoney()) {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Creditos Insuficientes! ");
                this.taskGroupState.info = "PAUSE";
                notifyAllWorkers();
                return null;
            }
            if (blocks.size() > 0) {
                for (Block block : blocks) {
                    if (!block.isFinished && !block.isOcupied) {
                        return block;
                    }
                }
            }
            blocks.add(aux = new Block(false, true, blocks.size() * delta, end_of_block, isFinished));
            return aux;
        }

    }

    public State getState() throws RemoteException {
        return this.taskGroupState;
    }

    public void setState(State newState) throws RemoteException {
        this.taskGroupState = newState;
        notifyAllWorkers();
    }

    public void notifyAllWorkers() throws RemoteException {
        for (ArrayList<WorkerRI> workersRI : associatedWorkers.values()) {
            for (WorkerRI workerRI : workersRI) {
                workerRI.update();
            }
        }
    }

    @Override
    public synchronized void discoveredHash(String hash, int index, WorkerRI worker) throws RemoteException, InterruptedException {
        hashedCodesFound.add(hash);
        this.availableCredits = availableCredits - 10;
        worker.addCredits(10);
        dbMockup.getUser(worker.getUser().getUserName()).addCredits(10);
        hashedCodes.set(index, null);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Hash of word " + hash + " discovered!");
        if(hashedCodesFound.size() == hashedCodes.size()){
            this.taskGroupState.info = "DELETE";
            notifyAllWorkers();
        }
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
        if (user.getUserName().equals(this.owner.getUserName())) {
            this.taskGroupState.info = "PAUSE";
            notifyAllWorkers();
        }

    }
    public void resumeTaskWork(User user) throws RemoteException {
        if (!isFinished) {
            if (user.getUserName().equals(this.owner.getUserName())) {
                for (ArrayList<WorkerRI> workersRI : associatedWorkers.values()) {
                    for (WorkerRI workerRI : workersRI) {
                        workerRI.resumeThread();
                    }
                }
            }
        } else {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Não é possivel continuar Task Work");
        }
    }


    @Override
    public void clearMyWorks(User user) throws RemoteException {
        associatedWorkers.remove(user.getUserName());
    }

    @Override
    public void saveBlock(Block block) throws RemoteException {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Block save in line: " + block.startLine);
        int aux = (int) ((block.endLine + 1) / delta) - 1;
        blocks.get(aux).startLine = block.startLine;
        blocks.get(aux).isOcupied = false;

    }

    @Override
    public void endBlock(Block block, WorkerRI work) throws RemoteException {
        int auxCredits = (int) ((block.endLine + 1) - block.startLine);
        this.availableCredits = availableCredits - auxCredits;
        work.addCredits(auxCredits);
        dbMockup.getUser(work.getUser().getUserName()).addCredits(auxCredits);
        int aux = (int) ((block.endLine + 1) / delta) - 1;
        if (aux == -1) {
            aux = 0;
        }
        blocks.get(aux).isOcupied= false;
        blocks.get(aux).isFinished= true;
        if (block.isLast) {
            endAllThreads();
        }
    }

    public boolean endTaskWork(User user) throws RemoteException {
        if (this.owner.getUserName().compareTo(user.getUserName()) == 0) {
            endAllThreads();
            for (User user1 : associatedUsers.values()) {
                user1.getAssociatedTaskGroups().remove(this);
            }
            dbMockup.getUser(owner.getUserName()).addCredits(availableCredits);
            this.availableCredits = 0;
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Task Work apagada");
            return true;
        } else {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Não tem permissões para apagar Task Work ");
            return false;
        }
    }

    @Override
    public String getName() throws RemoteException {
        return this.name;
    }

    @Override
    public ArrayList<String> getWordsFound(User user) throws RemoteException {
        if(user.getUserName().compareTo(owner.getUserName())== 0){
           return hashedCodesFound;
        }else {
            return null;
        }
    }


    private boolean checkMoney() {
        int cont = 1;
        for (Block block : blocks) {
            if (block.isOcupied) {
                cont++;
            }
        }
        return availableCredits >= cont * delta + (hashedCodes.size() * 10) * cont;
    }

    private void endAllThreads() throws RemoteException {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Encerrar todas as threads da Taskwork ");
        this.taskGroupState.info = "DELETE";
        notifyAllWorkers();
    }



}
