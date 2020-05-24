package ProjetoSD.hashmatchproj.server;

import ProjetoSD.hashmatchproj.client.WorkerRI;

import java.rmi.Remote;

public interface HashMatchTaskGroupRI extends Remote {

    public void associateUser(User user);
    public void associateWorker(WorkerRI workerRI, User user);
}
