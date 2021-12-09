package frontend;

import api.IServer;
import client.Client;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.MembershipListener;
import org.jgroups.View;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.util.RspList;
import utility.GroupUtilities;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;



public class Frontend extends UnicastRemoteObject implements IServer, MembershipListener {

    public static final long serialVersionUID = 7337;

    public final String SERVER_NAME = "auction_site";
    public final int REGISTRY_PORT = 1099;

    private JChannel groupChannel;
    private RpcDispatcher dispatcher;

    private final int DISPATCHER_TIMEOUT = 1000;

    public Frontend() throws RemoteException {
        // Connect to the group (channel)
        this.groupChannel = GroupUtilities.connect();
        if (this.groupChannel == null) {
            System.exit(1); // error to be printed by the 'connect' function
        }

        // Bind this server instance to the RMI Registry
        this.bind(this.SERVER_NAME);

        // Make this instance of Frontend a dispatcher in the channel (group)
        this.dispatcher = new RpcDispatcher(this.groupChannel, this);
        this.dispatcher.setMembershipListener(this);

    }

    private void bind(String serverName) {
        try {
            Registry registry = LocateRegistry.createRegistry(this.REGISTRY_PORT);
            registry.rebind(serverName, this);
            System.out.println("✅    rmi server running...");
        } catch (Exception e) {
            System.err.println("🆘    exception:");
            e.printStackTrace();
            System.exit(1);
        }
    }



    public void viewAccepted(View newView) {
        System.out.printf("👀    jgroups view changed\n✨    new view: %s\n", newView.toString());
    }

    public void suspect(Address suspectedMember) {
        System.out.printf("👀    jgroups view suspected member crash: %s\n", suspectedMember.toString());
    }

    public void block() {
        System.out.printf("👀    jgroups view block indicator\n");
    }

    public void unblock() {
        System.out.printf("👀    jgroups view unblock indicator\n");
    }

    @Override
    public int createListing(int AuctionId, String itemDescription, int startingPrice, int reservePrice, Client seller) throws RemoteException {
        try {
            RspList<Integer> responses = this.dispatcher.callRemoteMethods(null, "createListing",
                    new Object[]{AuctionId, itemDescription, startingPrice, reservePrice, seller}, new Class[]{int.class, String.class, int.class, int.class, Client.class},
                    new RequestOptions(ResponseMode.GET_ALL, this.DISPATCHER_TIMEOUT));

            System.out.printf("️⃣    received %d responses from the group\n", responses.size());
            if (responses.isEmpty()) {
                return -1;
            }
            //TODO Check responses are the same
            return responses.getResults().get(0);

        } catch(Exception e){
            System.err.println("   dispatcher exception:");
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public String closeListing(int itemId, Client seller) throws RemoteException {
        try {
            RspList<String> responses = this.dispatcher.callRemoteMethods(null, "closeListing",
                    new Object[]{itemId, seller}, new Class[]{int.class, Client.class},
                    new RequestOptions(ResponseMode.GET_ALL, this.DISPATCHER_TIMEOUT));

            System.out.printf("️⃣    received %d responses from the group\n", responses.size());
            if (responses.isEmpty()) {
                return "Empty";
            }
            //TODO Check responses are the same
            return responses.getResults().get(0);

        } catch(Exception e){
            System.err.println("   dispatcher exception:");
            e.printStackTrace();
            return "Error";
        }
    }

    @Override
    public String displayListings() throws RemoteException {
        try {
            RspList<String> responses = this.dispatcher.callRemoteMethods(null, "displayListings",
                    null, null,
                    new RequestOptions(ResponseMode.GET_ALL, this.DISPATCHER_TIMEOUT));
            System.out.printf("️⃣    received %d responses from the group\n", responses.size());
            if (responses.isEmpty()) {
                return "Empty";
            }
            //TODO Check responses are the same
            return responses.getResults().get(0);
        } catch(Exception e){
            System.err.println("   dispatcher exception:");
            e.printStackTrace();
            return "Error";
        }
    }

    @Override
    public String bid(int bidAmount, int itemID, Client buyer) throws RemoteException {
        try {
            RspList<String> responses = this.dispatcher.callRemoteMethods(null, "bid",
                    new Object[]{bidAmount,itemID, buyer}, new Class[]{int.class, int.class, Client.class},
                    new RequestOptions(ResponseMode.GET_ALL, this.DISPATCHER_TIMEOUT));

            System.out.printf("️⃣    received %d responses from the group\n", responses.size());
            if (responses.isEmpty()) {
                return "Empty";
            }
            //TODO Check responses are the same
            return responses.getResults().get(0);

        } catch(Exception e){
            System.err.println("   dispatcher exception:");
            e.printStackTrace();
            return "Error";
        }

    }

    @Override
    public String registerUser(Client newUser) throws RemoteException {
        try {
            RspList<String> responses = this.dispatcher.callRemoteMethods(null, "registerUser",
                    new Object[]{newUser}, new Class[]{Client.class},
                    new RequestOptions(ResponseMode.GET_ALL, this.DISPATCHER_TIMEOUT));

            System.out.printf("️⃣    received %d responses from the group\n", responses.size());
            if (responses.isEmpty()) {
                return "Empty";
            }
            //TODO Check responses are the same
            return responses.getResults().get(0);

        } catch(Exception e){
            System.err.println("   dispatcher exception:");
            e.printStackTrace();
            return "Error";
        }
    }

    @Override
    public Client getUser(String email) throws RemoteException {
        try {
            RspList<Client> responses = this.dispatcher.callRemoteMethods(null, "getUser",
                    new Object[]{email}, new Class[]{String.class},
                    new RequestOptions(ResponseMode.GET_ALL, this.DISPATCHER_TIMEOUT));

            System.out.printf("️⃣    received %d responses from the group\n", responses.size());
            if (responses.isEmpty()) {
                return null;
            }
            //TODO Check responses are the same
            return responses.getResults().get(0);

        } catch(Exception e){
            System.err.println("   dispatcher exception:");
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        try {
            new Frontend();
        } catch (RemoteException e) {
            System.err.println("🆘    remote exception:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
