package ProjetoSD.hashmatchproj.client;
import ProjetoSD.hashmatchproj.server.HashMatchFactoryRI;
import ProjetoSD.hashmatchproj.server.HashMatchSessionRI;
import ProjetoSD.hashmatchproj.server.HashMatchTaskGroupRI;
import ProjetoSD.hashmatchproj.models.User;
import ProjetoSD.hashmatchproj.util.rmisetup.SetupContextRMI;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HashMatchMainClient{
    /**
     * Context for connecting a RMI client to a RMI Servant
     */
    private SetupContextRMI contextRMI;
    /**
     * Remote interface that will hold the Servant proxy
     */
    private HashMatchFactoryRI hashMatchFactoryRI;
    User user;
    HashMap<String,ArrayList<Thread>> createdThreads = new HashMap<>();
    HashMap<String, ArrayList<Worker>> createdWorkers = new HashMap<>();
    HashMatchSessionRI sessionRI = null;

    public static void main(String[] args) {
        if (args != null && args.length < 2) {
            System.err.println("usage: java [options] edu.ufp.sd.inf.rmi.edu.ufp.inf.sd.rmi.helloworld.server.HelloWorldClient <rmi_registry_ip> <rmi_registry_port> <service_name>");
            System.exit(-1);
        } else {
            //1. ============ Setup client RMI context ============
            HashMatchMainClient hwc = new HashMatchMainClient(args);
            //2. ============ Lookup service ============
            hwc.lookupService();
            //3. ============ Play with service ============
            hwc.playService();
        }
    }

    public HashMatchMainClient(String[] args) {
        try {
            //List ans set args
            SetupContextRMI.printArgs(this.getClass().getName(), args);
            String registryIP = args[0];
            String registryPort = args[1];
            String serviceName = args[2];
            //Create a context for RMI setup
            contextRMI = new SetupContextRMI(this.getClass(), registryIP, registryPort, new String[]{serviceName});
        } catch (RemoteException e) {
            Logger.getLogger(HashMatchMainClient.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private Remote lookupService() {
        try {
            //Get proxy to rmiregistry
            Registry registry = contextRMI.getRegistry();
            //Lookup service on rmiregistry and wait for calls
            if (registry != null) {
                //Get service url (including servicename)
                String serviceUrl = contextRMI.getServicesUrl(0);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "going to lookup service @ {0}", serviceUrl);

                //============ Get proxy to HelloWorld service ============
                hashMatchFactoryRI = (HashMatchFactoryRI) registry.lookup(serviceUrl);
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "registry not bound (check IPs). :(");
                //registry = LocateRegistry.createRegistry(1099);
            }
        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return hashMatchFactoryRI;
    }

    private void playService() {
        try {
            Scanner input = new Scanner(System.in);
            String login, password;
            boolean cycle = true;
            while (cycle) {
                System.out.println("Escolha uma opção:\n1 : Login\n2 : Registo\n0 : Sair");
                switch (input.nextInt()) {
                    case 0:
                        cycle = false;
                        return;
                    case 1:
                        System.out.println("Introduza o login:");
                        login = input.next();
                        System.out.println("Introduza a password:");
                        password = input.next();
                        sessionRI = this.hashMatchFactoryRI.login(login, password);
                        if (sessionRI != null) {
                            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Login Successeful! ");
                            user = sessionRI.getUser();
                            secondMenu(input, sessionRI);
                        } else
                            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Account Already Logged!");
                        break;
                    case 2:
                        System.out.println("Introduza o login:");
                        login = input.next();
                        System.out.println("Introduza a password:");
                        password = input.next();
                        hashMatchFactoryRI.register(login, password);
                        if (this.hashMatchFactoryRI.register(login, password)) {
                            sessionRI = this.hashMatchFactoryRI.login(login, password);
                        }
                        if (sessionRI != null) {
                            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Login Successeful! ");
                            secondMenu(input, sessionRI);
                        } else {
                            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Account Already Logged!");
                        }
                        break;
                }
            }
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "going to finish, bye. ;)");
        } catch (RemoteException | InterruptedException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Erro inesperado", ex);
            playService();
        }
    }

    private void secondMenu(Scanner input, HashMatchSessionRI sessionRI) throws RemoteException, InterruptedException {
        HashMatchTaskGroupRI taskGroupRI;
        ArrayList<String> hashCodes = new ArrayList<>();
        String taskGroupName;
        int credits;
        int N_lines= 1300000;

        hashCodes.add("31bca02094eb78126a517b206a88c73cfa9ec6f704c7030d18212cace820f025f00bf0ea68dbf3f3a5436ca63b53bf7bf80ad8d5de7d8359d0b7fed9dbc3ab99");
        hashCodes.add("77b4656300cd63110def4a7557f9313441192f99883675239b196b5dd5fc97cf571119a43ab62647f7ed98f785bc9befabe87b3de8215f4eb1a0d3ebe074d7b5");
        hashCodes.add("3ed8f41f9e0fffc26531ca9ac8d4f51c995bab4fae4f7374bc1b76e9456aa60cfb3a073f00a936e7ec7c7ae1bba7457c28f77db380bf0b294f689c0533c0f4d8");
        boolean cycle = true;
        while (cycle) {
            System.out.println("Escolha uma opção:\n1 : Criar Grupo de Trabalho\n2 : Listar Grupos de Trabalho\n3 : Juntar a Grupo de Trabalho\n4 : Entrar no Menu de um TaskGroup \n5 : Mostrar saldo\n0 : Voltar");
            switch (input.nextInt()) {
                case 0:
                    cycle = false;
                    break;
                case 1:
                    System.out.println("Escolha um nome para o Task Group: \n");
                    taskGroupName = input.next();
                    System.out.println("Insira o numero de creditos que pretende adicionar ao Task Group: \n");
                    credits = input.nextInt();
                    taskGroupRI = sessionRI.createHashMatchTaskGroup(user, "SHA-512", "https://raw.githubusercontent.com/danielmiessler/SecLists/master/Passwords/darkc0de.txt", hashCodes, taskGroupName, credits, N_lines);
                    if (taskGroupRI != null)
                        taskGroupMenu(input, taskGroupRI);
                    break;
                case 2:
                    for (String aux : sessionRI.getTaskGroupsName()) {
                        System.out.println(aux);
                    }
                    break;
                case 3:
                    for (String aux : sessionRI.getTaskGroupsName()) {
                        System.out.println(aux);
                    }
                    System.out.println("Escolha um Task Group:");
                    sessionRI.joinTaskGroup(input.next());
                    break;
                case 4:
                    System.out.println("Escolha uma taskGroup para entrar: ");
                    taskGroupRI = sessionRI.enterTaskGroupMenu(input.next());
                    if (taskGroupRI != null)
                        taskGroupMenu(input, taskGroupRI);
                    break;
                case 5:
                    this.user= sessionRI.getUser();
                    user.printCredits();
                    break;
            }
        }
    }

    private void taskGroupMenu(Scanner input, HashMatchTaskGroupRI taskGroupRI) throws RemoteException, InterruptedException {
        Worker worker;
        int nrOfThreads;
        ArrayList<WorkerRI> createdWorkersRI = new ArrayList<>();
        ArrayList<Worker> auxArrayWorkers = new ArrayList<>();
        ArrayList<Thread> auxArrayThreads= new ArrayList<>();
        boolean cycle = true;
        Thread thread;
        Worker workeraux;
        String nameTaskWork= taskGroupRI.getName();
        while (cycle) {
            System.out.println("Escolha uma opção:\n 1: Descobrir Palavras-Chave\n 2: Parar Task Group \n 3: Destruir Workers na TaskWork \n 4: Retomar Task Group\n 5: Apagar TaskWork\n6: Mostrar Palavras encontradas");
            switch (input.nextInt()) {
                case 1:
                    System.out.println("Quantas Threads quer criar? (Escolha de acordo com o número de Threads do seu CPU para máximo de eficiencia!)");
                    nrOfThreads = input.nextInt();
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Saving Threads...");
                    for (int i = 0; i < nrOfThreads; i++) {
                        auxArrayWorkers.add(workeraux = new Worker(taskGroupRI,user));
                        thread = new Thread((workeraux));
                        workeraux.thread = thread;
                        auxArrayThreads.add(thread);
                    }
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Threads saved successfully!");
                    createdWorkersRI.addAll(auxArrayWorkers);
                    if(!taskGroupRI.associateWorkers(createdWorkersRI, this.user)){
                        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Não se aceitam mais Workers, Task Work terminada ");
                    }else{
                        createdWorkers.put(nameTaskWork,auxArrayWorkers);
                        createdThreads.put(nameTaskWork, auxArrayThreads);
                        int i = 0;
                        for (Thread threadaux : createdThreads.get(nameTaskWork)) {
                            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Starting Thread "+i);
                            threadaux.start();
                            i++;
                        }
                    }
                    break;
                case 2:
                    if(this.user.getUserName().equals( taskGroupRI.getOwner().getUserName())){
                        taskGroupRI.stopTaskWork(this.user);
                    }else{
                        System.out.println(" Sem permissão" );
                    }
                    break;
                case 3:
                    for(Worker worker1 : createdWorkers.get(nameTaskWork)){
                        sessionRI.addCredits(worker1.credits);
                        worker1.endThread();
                    }
                    taskGroupRI.clearMyWorks(user);

                case 4:
                    if(this.user.getUserName().equals( taskGroupRI.getOwner().getUserName())){
                        taskGroupRI.resumeTaskWork(this.user);
                    }else{
                        System.out.println(" Sem permissão" );
                    }
                    break;
                case 5:
                    if(sessionRI.endTaskWork(user,nameTaskWork)){
                        System.out.println(" TaskWork Apagada" );
                    }else{
                        System.out.println(" Sem permissão" );
                    }
                    break;
                case 6:
                      ArrayList<String> aux= taskGroupRI.getWordsFound(user);
                      if(aux != null){
                          for(String string : aux){
                              System.out.println("Palavra: "+string);
                          }
                      }else{
                          System.out.println(" Sem permissão" );
                      }
                    break;
                case 0:
                    cycle = false;
                    break;
            }
        }
    }
}
