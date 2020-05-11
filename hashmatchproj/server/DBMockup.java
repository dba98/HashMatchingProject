package ProjetoSD.hashmatchproj.server;

import ProjetoSD.hashmatchproj.models.User;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class DBMockup implements Serializable {

    ArrayList<User> users = new ArrayList<>();
    ArrayList<File> files = new ArrayList<>();

    public DBMockup(){
            users.add(new User("Diogo","12345"));
            users.add(new User("Ricardo","12345"));

    }

    public boolean exists(String userName, String password){
        for (User user : this.users){
            if(user.getUserName().compareTo(userName) == 0 && user.getPassword().compareTo(password) == 0){
                return true;
            }
        }
        return false;
    }

    public void register(String userName, String password){
        if(!exists(userName,password)){
            users.add(new User(userName,password));
        }
    }

}
