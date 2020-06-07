package ProjetoSD.hashmatchproj.models;

import ProjetoSD.hashmatchproj.server.HashMatchTaskGroupImpl;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private String userName;
    private String password;
    private ArrayList<HashMatchTaskGroupImpl> associatedTaskGroups = new ArrayList<>();
    private int credits;

    public User(String userName, String password){
        this.userName = userName;
        this.password = password;
        this.credits= 0;
    }

    public ArrayList<HashMatchTaskGroupImpl> getAssociatedTaskGroups() {
        return associatedTaskGroups;
    }

    public void setAssociatedTaskGroups(ArrayList<HashMatchTaskGroupImpl> associatedTaskGroups) {
        this.associatedTaskGroups = associatedTaskGroups;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
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
