package ProjetoSD.hashmatchproj.server;

import ProjetoSD.hashmatchproj.models.User;

import java.rmi.Remote;

public interface HashMatchTaskGroupRI extends Remote {

    public void associateUser(User user);
    public void associateWorker(Thread thread, User user);
}
