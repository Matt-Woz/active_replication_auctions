package client;

import api.IServer;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class main {
    public final String SERVER_NAME = "auction_site";



    public main(){
        try{
            Registry registry = LocateRegistry.getRegistry();
            IServer server = (IServer) registry.lookup(this.SERVER_NAME);
            Authentication authentication = new Authentication();
            authentication.menu(server);
        } catch (Exception e) {
            System.err.println("🆘 exception:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        new main();
        }
    }
