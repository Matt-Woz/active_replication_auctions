package backend;

import client.Client;
import org.jgroups.JChannel;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.util.RspList;
import utility.GroupUtilities;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class Backend {

    private final int DISPATCHER_TIMEOUT = 1000;
    private JChannel groupChannel;
    private RpcDispatcher dispatcher;
    private ConcurrentHashMap<Integer, AuctionItem> items;
    private ConcurrentHashMap<String, Client> users;

    public Backend() throws Exception {

        // Connect to the group (channel)
        this.groupChannel = GroupUtilities.connect();
        if (this.groupChannel == null) {
            System.exit(1); // error to be printed by the 'connect' function
        }
        // Make this instance of Backend a dispatcher in the channel (group)
        this.dispatcher = new RpcDispatcher(this.groupChannel, this);

        RspList<ConcurrentHashMap<Integer, AuctionItem>> itemResponse = dispatcher.callRemoteMethods(null, "getItems",
                null, null,
                new RequestOptions(ResponseMode.GET_ALL, DISPATCHER_TIMEOUT));
        RspList<ConcurrentHashMap<String, Client>> userResponse = dispatcher.callRemoteMethods(null, "getUsers",
                null, null,
                new RequestOptions(ResponseMode.GET_ALL, DISPATCHER_TIMEOUT));

        ConcurrentHashMap<Integer, AuctionItem> tempAuctionmap;
        if(itemResponse.isEmpty()) {
            tempAuctionmap=new ConcurrentHashMap<Integer,AuctionItem>();
        }
        else{
            tempAuctionmap = itemResponse.getResults().get(0);
        }
        items = tempAuctionmap;
        ConcurrentHashMap<String,Client> tempClientmap;
        System.out.println("The client list "+ userResponse.getResults());
        if(userResponse.isEmpty()) {
            tempClientmap=new ConcurrentHashMap<String,Client>();
        }
        else{
            tempClientmap = userResponse.getResults().get(0);
        }
        users = tempClientmap;

    }

    public int kill()
    {
        System.exit(0);
        return 0;
    }

    public int createListing(String itemDescription, int startingPrice, int reservePrice, Client seller) {
            int i = 0;
            while(items.containsKey(i)){
                i++;
            }
            AuctionItem newItem = new AuctionItem(i,itemDescription, startingPrice, reservePrice, seller);
            items.put(i, newItem);
            int result = newItem.getAuctionId();
            return result;
        }

    public String closeListing(int itemId, Client seller) {
        if(items.get(itemId) == null)
        {
            return "Error: item does not exist.\n";
        }
        if(!items.get(itemId).getSeller().getEmail().equalsIgnoreCase(seller.getEmail()))
        {
            return "Error: you did not create this auction.\n";
        }
        if(items.get(itemId).getHighestBidder() == null)
        {
            items.remove(itemId);
            return "Item failed to sell - No bidders.\n";
        }
        if(items.get(itemId).getHighestBid() > items.get(itemId).getReservePrice())
        {
            AuctionItem item = items.get(itemId);
            items.remove(itemId);
            return "The winner of the item is: " + item.getHighestBidder().toString();
        }
        else
        {
            items.remove(itemId);
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
            return "Highest bid higher or equal to your bidding price.\n";
        }
    }

    public String registerUser(Client newUser) {
        String email = newUser.getEmail();
        boolean duplicate = checkEmail(users, email);
        if(duplicate)
        {
            return "Error - Email is already registered.\n";
        }
        users.put(newUser.getEmail(), newUser);
        return "User successfully registered.\n";
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
        for(Map.Entry mapElement : users.entrySet())
        {
            String oldEmail = (String) mapElement.getKey();
            if (oldEmail.equalsIgnoreCase(Email))
            {
                return true;
            }
        }
        return false;

    }

    public static void main(String[] args) throws Exception {
        new Backend();
    }

}