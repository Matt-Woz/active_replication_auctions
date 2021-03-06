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
import org.jgroups.util.Rsp;
import org.jgroups.util.RspList;
import utility.GroupUtilities;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;


public class Frontend extends UnicastRemoteObject implements IServer, MembershipListener {
    public static final long serialVersionUID = 7337;
    public final String SERVER_NAME = "auction_site";
    public final int REGISTRY_PORT = 1099;
    private JChannel groupChannel;
    private RpcDispatcher dispatcher;
    private final int DISPATCHER_TIMEOUT = 1000;

    public Frontend() throws RemoteException {
        this.groupChannel = GroupUtilities.connect();
        if (this.groupChannel == null) {
            System.exit(1);
        }
        this.bind(this.SERVER_NAME);
        this.dispatcher = new RpcDispatcher(this.groupChannel, this);
        this.dispatcher.setMembershipListener(this);

    }

    private void bind(String serverName) {
        try {
            Registry registry = LocateRegistry.createRegistry(this.REGISTRY_PORT);
            registry.rebind(serverName, this);
            System.out.println("ā    rmi server running...");
        } catch (Exception e) {
            System.err.println("š    exception:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void viewAccepted(View newView) {
        System.out.printf("š    jgroups view changed\nāØ    new view: %s\n", newView.toString());
    }

    public void suspect(Address suspectedMember) {
        System.out.printf("š    jgroups view suspected member crash: %s\n", suspectedMember.toString());
    }

    public void block() {
        System.out.printf("š    jgroups view block indicator\n");
    }

    public void unblock() {
        System.out.printf("š    jgroups view unblock indicator\n");
    }

    /**
     * This method creates the instance of an auctionItem and stores it in a hashmap
     *
     * @param itemDescription Description of item as written by seller creating it
     * @param startingPrice   Starting price of auction item
     * @param reservePrice    Reserve price of auction item
     * @param seller          Seller client creating the listing
     * @return Id of created item
     */
    @Override
    public int createListing(String itemDescription, int startingPrice, int reservePrice, Client seller) throws RemoteException {
        RspList<Integer> responses = null;
        try {
            responses = this.dispatcher.callRemoteMethods(null, "createListing",
                    new Object[]{itemDescription, startingPrice, reservePrice, seller}, new Class[]{String.class, int.class, int.class, Client.class},
                    new RequestOptions(ResponseMode.GET_ALL, this.DISPATCHER_TIMEOUT));

            System.out.printf("  received %d responses from the group\n", responses.size());
            if (responses.isEmpty()) {
                return -1;
            }
        } catch(Exception e){
            System.err.println("   dispatcher exception:");
            e.printStackTrace();
        }
        return (int) checkEquality(responses);
    }
    /**
     * This method does some validity and authentication checks and closes the listing
     *
     * @param itemId auction Id of the item
     * @param seller Seller calling for closing of the auction
     * @return Success or error message
     */
    @Override
    public String closeListing(int itemId, Client seller) throws RemoteException {
        RspList<String> responses = null;
        try {
            responses = this.dispatcher.callRemoteMethods(null, "closeListing",
                    new Object[]{itemId, seller}, new Class[]{int.class, Client.class},
                    new RequestOptions(ResponseMode.GET_ALL, this.DISPATCHER_TIMEOUT));
            System.out.printf("  received %d responses from the group\n", responses.size());
            if (responses.isEmpty()) {
                return "Empty";
            }
        } catch(Exception e){
            System.err.println("   dispatcher exception:");
            e.printStackTrace();
        }
        return (String) checkEquality(responses);
    }
    /**
     * This method displays the items currently up for auction on the server
     * @return String of listed items up for auction
     */
    @Override
    public String displayListings() throws RemoteException {
        RspList<String> responses = null;
        try {
            responses = this.dispatcher.callRemoteMethods(null, "displayListings",
                    null, null,
                    new RequestOptions(ResponseMode.GET_ALL, this.DISPATCHER_TIMEOUT));
            System.out.printf("   received %d responses from the group\n", responses.size());
            if (responses.isEmpty()) {
                return "Empty";
            }
        } catch(Exception e){
            System.err.println("   dispatcher exception:");
            e.printStackTrace();
        }
        return (String) checkEquality(responses);
    }
    /**
     * This method does validity checks on the bid just placed and sets the bid, depending on the checks
     *
     * @param bidAmount Amount which bidder places
     * @param itemID    AuctionID of the item
     * @param buyer     Instance of the client making the bid
     * @return Success or error message
     */
    @Override
    public String bid(int bidAmount, int itemID, Client buyer) throws RemoteException {
        RspList<String> responses = null;
        try {
            responses = this.dispatcher.callRemoteMethods(null, "bid",
                    new Object[]{bidAmount,itemID, buyer}, new Class[]{int.class, int.class, Client.class},
                    new RequestOptions(ResponseMode.GET_ALL, this.DISPATCHER_TIMEOUT));
            System.out.printf(" received %d responses from the group\n", responses.size());
            if (responses.isEmpty()) {
                return "Empty";
            }
        } catch(Exception e){
            System.err.println("   dispatcher exception:");
            e.printStackTrace();
        }
        return (String) checkEquality(responses);

    }
    /**
     * This method does validity checks on the new client and adds the user to the hashmap
     *
     * @param newUser Instance of the newly created user
     * @return Success/error message
     */
    @Override
    public String registerUser(Client newUser) throws RemoteException {
        RspList<String> responses = null;
        try {
            responses = this.dispatcher.callRemoteMethods(null, "registerUser",
                    new Object[]{newUser}, new Class[]{Client.class},
                    new RequestOptions(ResponseMode.GET_ALL, this.DISPATCHER_TIMEOUT));
            System.out.printf("  received %d responses from the group\n", responses.size());
            if (responses.isEmpty()) {
                return "Empty";
            }
        } catch(Exception e){
            System.err.println("   dispatcher exception:");
            e.printStackTrace();
        }
        return (String) checkEquality(responses);
    }
    /**
     * This method gets the client with the associated email
     *
     * @param email Email associated to client you want to get
     * @return Instance of client with associated email
     */
    @Override
    public Client getUser(String email) throws RemoteException {
        RspList<Client> responses = null;
        try {
            responses = this.dispatcher.callRemoteMethods(null, "getUser",
                    new Object[]{email}, new Class[]{String.class},
                    new RequestOptions(ResponseMode.GET_ALL, this.DISPATCHER_TIMEOUT));
            System.out.printf("  received %d responses from the group\n", responses.size());
            if (responses.isEmpty()) {
                return null;
            }
        } catch(Exception e){
            System.err.println("   dispatcher exception:");
            e.printStackTrace();
        }
        return (Client) checkEquality(responses);
    }

    /**
     * This method checks that the responses from the backend are the same, if not, call the killServer method
     * @param response List of responses from the backend
     * @return Object returned by method as defined in RMI server interface
     */
    private Object checkEquality(RspList response)
    {
        List list = response.getResults();
        Boolean isEqual = list.stream().distinct().limit(list.size()).count() <= 1;
        if(isEqual)
        {
            return response.getResults().get(0);
        }
        else{
            return killServer(response);
        }
    }

    /**
     * This method kills any wrong servers (which do not have the modal response)
     * @param response List of responses by backend
     * @return The modal response, or the response from the oldest server if there is no mode.
     */
    private Object killServer(RspList response)
    {
        Object mode = findModalResponse(response);
        for(Iterator it = response.entrySet().iterator(); it.hasNext();){
            Map.Entry entry = (Map.Entry) it.next();
            Address add = (Address) entry.getKey();
            Rsp rsp = (Rsp) entry.getValue();
            Object object = rsp.getValue();
            if(!Objects.equals(object,mode)){
                try{
                    this.dispatcher.callRemoteMethod(add, "kill",
                            null, null,
                            new RequestOptions(ResponseMode.GET_NONE, this.DISPATCHER_TIMEOUT));
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        return mode;
    }

    /**
     * This method finds the most common (modal) response from a list of responses
     * @param response List of backend responses
     * @return Object the most common response, or the oldest if there is no majority
     */
    private Object findModalResponse(RspList response)
    {
        List results = response.getResults();
        int n = results.size();
        int maxCount = 0;
        int index = -1;
        for(int i = 0; i < n; i++){
            int count = 0;
            for (int j = 0; j < n; j++){
                if(results.get(i).equals(results.get(j)))
                    count++;
            }
            if(count > maxCount){
                maxCount = count;
                index = i;
            }
        }
        return results.get(index);
    }

    public static void main(String[] args) {
        try {
            new Frontend();
        } catch (RemoteException e) {
            System.err.println("š    remote exception:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
