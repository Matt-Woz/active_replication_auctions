package client;

import api.IServer;

import java.rmi.RemoteException;
import java.util.Scanner;

/**
 * This class is used for the interface and logic for the buyer options
 */
public class buyerMenu {
    private final Scanner scn = new Scanner(System.in);

    /**
     * This method provides an interface for the buyer to view and bid on listings
     * @param server Server which client is connected to
     * @param buyer Client which can bid on listings
     */
    public void menu(IServer server, Client buyer) throws RemoteException {
        while(true)
        {
            System.out.println("Buyer menu: ");
            System.out.println("1\t Bid on listing");
            System.out.println("2\t View Listings");
            System.out.println("3\t Exit");
            System.out.println("Please enter your choice:");
            int option = scn.nextInt();
            switch (option)
            {
                case 1:
                    bid(server, buyer);
                    break;
                case 2:
                    display(server);
                    break;
                case 3:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option");
            }
        }
    }

    /**
     * This method allows the user to bid on auctions and calls a server method to register the bid
     * @param server Server which client is connected to
     * @param buyer Client bidding on the lisintg
     * @throws RemoteException
     */
    public void bid(IServer server, Client buyer) throws RemoteException {
        System.out.println("Please enter the itemID of the item you want to bid on:");
        int itemID = scn.nextInt();
        System.out.println("Please enter how much you want to bid on item " + itemID);
        int bidAmount = scn.nextInt();
        System.out.println(server.bid(bidAmount, itemID, buyer));

    }

    /**
     * This method calls on the server to return and print a list of the items currently up for for auction
     * @param server Server client is connected to
     */
    public void display(IServer server) throws RemoteException {
        System.out.println(server.displayListings());
    }






}

