package ProjetoSD.hashmatchproj.server;

import ProjetoSD.hashmatchproj.client.WorkerRI;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class HashMatchTaskGroupImpl implements HashMatchTaskGroupRI {

    User owner;
    HashMap<String, User> associatedUsers = new HashMap<>();
    HashMap<String, WorkerRI> associatedWorkers = new HashMap<>();
    ArrayList<String> hashedCodes;
    String workingFile;
    String hashAlg;

    public HashMatchTaskGroupImpl(User owner, String file,String hashAlg,ArrayList<String> hashedCodes) {
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

    public void associateWorker(WorkerRI workerRI, User user) {
       if(!associatedWorkers.containsKey(user.getUserName()+" "+user.nrWorkers)){
           associatedWorkers.put(user.userName+" "+user.nrWorkers++,workerRI);
           workerRI.setData(1L,5000L,hashAlg,hashedCodes);
       }
    }


}
