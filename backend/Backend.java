package backend;

import client.Client;
import org.jgroups.JChannel;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.util.RspList;
import utility.GroupUtilities;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;


public class Backend {

    private final int DISPATCHER_TIMEOUT = 1000;
    private JChannel groupChannel;
    private RpcDispatcher dispatcher;
    private int requestCount;
    private ConcurrentHashMap<Integer, AuctionItem> items;
    private ConcurrentHashMap<String, Client> users;

    public Backend() throws Exception {
        this.requestCount = 0;

        // Connect to the group (channel)
        this.groupChannel = GroupUtilities.connect();
        if (this.groupChannel == null) {
            System.exit(1); // error to be printed by the 'connect' function
        }
        // Make this instance of Backend a dispatcher in the channel (group)
        this.dispatcher = new RpcDispatcher(this.groupChannel, this);
        ConcurrentHashMap<Integer, AuctionItem> tempItemMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, Client> tempUserMap = new ConcurrentHashMap<>();

        RspList<ConcurrentHashMap<Integer, AuctionItem>> itemResponse = dispatcher.callRemoteMethods(null, "getItems",
                null, null,
                new RequestOptions(ResponseMode.GET_ALL, DISPATCHER_TIMEOUT));
        RspList<ConcurrentHashMap<String, Client>> userResponse = dispatcher.callRemoteMethods(null, "getUsers",
                null, null,
                new RequestOptions(ResponseMode.GET_ALL, DISPATCHER_TIMEOUT));
        System.out.println("ITEM RESPONSE:" + itemResponse.getResults().get(0) +"\n");
        System.out.println("USER RESPONSE:" + userResponse.getResults().get(0)+"\n");
        if (itemResponse.isEmpty()) {
            System.out.println("BLAH BLAH");
            items = tempItemMap;
        }
        else{
            System.out.println("NO BLAH BLAH");
            items = itemResponse.getResults().get(0);
        }
        if(userResponse.isEmpty()){
            System.out.println("USER BLAH BLAH");
            users = tempUserMap;
        }
        else{
            System.out.println("USER NO BLAH BLAH");
            users = userResponse.getResults().get(0);
        }
    }


    public int createListing(int AuctionID, String itemDescription, int startingPrice, int reservePrice, Client seller) {
        items.put(AuctionID, new AuctionItem(AuctionID, itemDescription, startingPrice, reservePrice, seller));
        int result = items.get(AuctionID).getAuctionId();
        return result;
    }

    public String closeListing(int itemId, Client seller) {
        if(items.get(itemId) == null)
        {
            return "Error: item does not exist.\n";
        }
        if(items.get(itemId).getSeller().getEmail().equalsIgnoreCase(seller.getEmail()))
        {
            return "Error: you did not create this auction.\n";
        }
        if(items.get(itemId).getHighestBidder() == null)
        {
            itemManager.getInstance().removeItem(itemId);
            return "Item failed to sell - No bidders.\n";
        }
        if(items.get(itemId).getHighestBid() > items.get(itemId).getReservePrice())
        {
            AuctionItem item = items.get(itemId);
            itemManager.getInstance().removeItem(itemId);
            return "The winner of the item is: " + item.getHighestBidder().toString();
        }
        else
        {
            itemManager.getInstance().removeItem(itemId);
            return "Item failed to sell - highest bid is not higher than reserve price.\n";
        }
    }

    public String displayListings() {
        ArrayList<String> itemsDisplay = new ArrayList<>();
        items.forEach((k, v) -> itemsDisplay.add(v.toString()));
        String result = itemsDisplay.toString();
        System.out.println(result);
        return result;
    }
    public String bid(int bidAmount, int itemID, Client buyer) {
        if(items.get(itemID) == null)
        {
            return "Error: item does not exist.\n";
        }
        if(bidAmount > items.get(itemID).getHighestBid() && bidAmount >= items.get(itemID).getStartingPrice())
        {
            items.get(itemID).setHighestBidder(buyer);
            items.get(itemID).setHighestBid(bidAmount);
            return "Bid successfully set.\n";
        }
        else
        {
            return "Highest bid higher than your bidding price.\n";
        }
    }

    public String registerUser(Client newUser) {
        System.out.println("GOODBYE");
        String email = newUser.getEmail();
        System.out.println("WARGWAN");
        boolean duplicate = checkEmail(users, email);
        System.out.println("HELLO!!!!!");
        if(duplicate)
        {
            return "Error - Email is already registered.\n";
        }
        String result = addUser(newUser);
        return result;
    }

    public Client getUser(String email) {
        Client result = users.get(email);
        return result;
    }

    public ConcurrentHashMap<Integer, AuctionItem> getItems() {
        return items;
    }

    public ConcurrentHashMap<String, Client> getUsers() {
        return users;
    }

    public boolean checkEmail(ConcurrentHashMap<String, Client> users, String Email)
    {
        return false;
        //return users.containsKey(Email);
        /*
        for(Map.Entry mapElement : users.entrySet())
        {
            String oldEmail = (String) mapElement.getKey();
            if (oldEmail.equalsIgnoreCase(Email))
            {
                return true;
            }
        }
        return false;*/

    }
    public String addUser(Client user)
    {
       // String output =  user.getName() + ',' + user.getEmail() + ',' + user.getPassword() + ',' + user.isSeller();
        users.put(user.getEmail(), user);
        //clientManager.writeToFile(output);
        return "User successfully registered.\n";
    }


    public static void main(String[] args) throws Exception {
        new Backend();
    }

}