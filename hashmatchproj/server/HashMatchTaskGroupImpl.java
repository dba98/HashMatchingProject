package ProjetoSD.hashmatchproj.server;

import ProjetoSD.hashmatchproj.models.User;
import ProjetoSD.hashmatchproj.models.Worker;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class HashMatchTaskGroupImpl implements HashMatchTaskGroupRI {

    User owner;
    HashMap<String, User> associatedUsers = new HashMap<>();
    HashMap<String, Worker> associatedWorkers = new HashMap<>();
    ArrayList<String> hashedCodes;
    File workingFile;
    String hashAlg;

    public HashMatchTaskGroupImpl(User owner, File file,String hashAlg,ArrayList<String> hashedCodes) {
        this.owner = owner;
        this.workingFile = file;
        this.hashAlg = hashAlg;
        this.hashedCodes = hashedCodes;
    }

    public void associateUser(User user) {
        if (!associatedUsers.containsKey(user.getUserName())) {
            associatedUsers.put(user.getUserName(), user);
        }
    }

    public void associateWorker(Thread thread, User user) {
        if (!associatedWorkers.containsKey(thread.getName().concat(user.getUserName()))) {
            Worker worker = new Worker(thread, user.getUserName());
            associatedWorkers.put(worker.name, worker);
        }
    }


}
