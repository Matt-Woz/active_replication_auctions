package api;

import client.Client;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * This is the RMI server interface which defines the methods implemented by the server which
 * can be run by client.
 * The implementation logic of these items is in the ServerLogic class
 */
public interface IServer extends Remote {
    /**
     * This method creates the instance of an auctionItem and stores it in a hashmap
     *
     * @param itemDescription Description of item as written by seller creating it
     * @param startingPrice   Starting price of auction item
     * @param reservePrice    Reserve price of auction item
     * @param seller          Seller client creating the listing
     */
    int createListing(String itemDescription, int startingPrice, int reservePrice, Client seller) throws RemoteException;

    /**
     * This method does some validity and authentication checks and closes the listing
     *
     * @param itemId auction Id of the item
     * @param seller Seller calling for closing of the auction
     * @return Success of error message
     */
    String closeListing(int itemId, Client seller) throws RemoteException;

    /**
     * This method displays the items currently up for auction on the server
     *
     * @return String of listed items up for auction
     */
    String displayListings() throws RemoteException;

    /**
     * This method does validity checks on the bid just placed and sets the bid, depending on the checks
     *
     * @param bidAmount Amount which bidder places
     * @param itemID    AuctionID of the item
     * @param buyer     Instance of the client making the bid
     * @return Success or error message
     */
    String bid(int bidAmount, int itemID, Client buyer) throws RemoteException;

    /**
     * This method does validity checks on the new client and adds the user to the hashmap
     *
     * @param newUser Instance of the newly created user
     * @return Success/error message
     */
    String registerUser(Client newUser) throws RemoteException;

    /**
     * This method gets the client with the associated email
     *
     * @param email Email associated to client you want to get
     * @return Instance of client with associated email
     */
    Client getUser(String email) throws RemoteException;

}