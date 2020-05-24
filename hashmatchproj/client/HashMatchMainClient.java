package ProjetoSD.hashmatchproj.client;


import ProjetoSD.hashmatchproj.server.User;
import ProjetoSD.hashmatchproj.server.HashMatchFactoryRI;
import ProjetoSD.hashmatchproj.server.HashMatchSessionRI;
import ProjetoSD.hashmatchproj.server.HashMatchTaskGroupRI;
import ProjetoSD.hashmatchproj.util.rmisetup.SetupContextRMI;

import java.io.File;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HashMatchMainClient {
    /**
     * Context for connecting a RMI client to a RMI Servant
     */
    private SetupContextRMI contextRMI;
    /**
     * Remote interface that will hold the Servant proxy
     */
    private HashMatchFactoryRI hashMatchFactoryRI;
    User user;
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

    public HashMatchMainClient(String args[]) {
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
            HashMatchSessionRI sessionRI = null;
            boolean cycle = true;
            while (cycle) {
                System.out.println("Escolha uma opção:\n1 : Login\n2 : Registo\n0 : Sair");
                switch (input.nextInt()) {
                    case 0:
                        cycle = false;
                        break;
                    case 1:
                        System.out.println("Introduza o login:");
                        login = input.next();
                        System.out.println("Introduza a password:");
                        password = input.next();
                        sessionRI = this.hashMatchFactoryRI.login(login, password);
                        if (sessionRI != null) {
                            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Login Successeful! ");
                            user = sessionRI.getUser(login,password);
                            secondMenu(input,sessionRI);
                        }
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
                            secondMenu(input,sessionRI);
                        }
                        break;
                }
            }
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "going to finish, bye. ;)");
        } catch (RemoteException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void secondMenu(Scanner input,HashMatchSessionRI sessionRI) throws RemoteException {
        HashMatchTaskGroupRI taskGroupRI;
        boolean cycle = true;
        while (cycle) {
            System.out.println("Escolha uma opção:\n1 : Criar Grupo de Trabalho\n2 : Listar Grupos de Trabalho\n 3: Juntar a Grupo de Trabalho\n0 : Voltar");
            switch (input.nextInt()) {
                case 0:
                    cycle = false;
                    break;
                case 1:
                    taskGroupRI = sessionRI.createHashMatchTaskGroup(user,"SHA-512",new File(""),null);
                    taskGroupMenu(input,taskGroupRI);
                    break;
                case 2:
                    sessionRI.listTaskGroups();
                    break;
                case 3:
                    break;
            }
        }
    }

    private void taskGroupMenu(Scanner input,HashMatchTaskGroupRI taskGroupRI){
        Worker worker;
        boolean cycle = true;
        while(cycle){
            System.out.println("Escolha uma opção:\n 1: Descobrir HashCodes");
            switch(input.nextInt()){
                case 1:
                    worker = new Worker();
                    taskGroupRI.associateWorker(worker,this.user);
                    System.out.println(worker.encryptionFormat);
                    break;
                case 0:
                    cycle = false;
                    break;
            }
        }
    }
}
