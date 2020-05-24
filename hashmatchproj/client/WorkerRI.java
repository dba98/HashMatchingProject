package ProjetoSD.hashmatchproj.client;

import java.rmi.Remote;
import java.util.ArrayList;

public interface WorkerRI extends Remote {

    void setData(long startLine,long delta,String encryptionFormat,ArrayList<String> hashCodes);
    void setStopThread(boolean doStop);
}
