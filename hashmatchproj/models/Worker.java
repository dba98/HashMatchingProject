package ProjetoSD.hashmatchproj.models;

public class Worker {

    public Thread thread;
    public String name;

    public Worker(Thread thread, String userName){
        this.thread = thread;
        this.name = thread.getName().concat(userName);
    }
}
