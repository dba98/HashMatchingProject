package ProjetoSD.hashmatchproj.server;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public class User implements Serializable {

    String userName;
    String password;
    ArrayList<HashMatchTaskGroupImpl> associatedTaskGroups = new ArrayList<>();
    int credits;
    int nrWorkers;

    public User(String userName, String password){
        this.userName = userName;
        this.password = password;
        this.credits= 0;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addCredits(int newCredits)  { this.credits = this.credits + newCredits; }

    public void printCredits()  {
        System.out.println("Saldo na conta: "+credits);
    }

}
