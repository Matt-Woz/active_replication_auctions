package client;

import api.IServer;

import java.rmi.RemoteException;
import java.util.Scanner;

/**
 * This class is used for the interface and logic for the seller options
 */
public class sellerMenu {
    private final Scanner scn = new Scanner(System.in);
    private static int auctionId = 0;

    /**
     * This method provides an interface for the seller to create, close and view current listings
     * @param server Server which the seller is connected to
     * @param seller Instance of the seller which is creating/closing listings
     */
    public void menu(IServer server, Client seller) throws RemoteException {
        while(true)
        {
            System.out.println("Seller menu: ");
            System.out.println("1\t Create Listing");
            System.out.println("2\t Close Listing");
            System.out.println("3\t View Listings");
            System.out.println("4\t Exit");
            System.out.println("Please enter your choice:");
            int choice = scn.nextInt();
            switch(choice){
                case 1: createListing(server, seller);
                    break;
                case 2: closeListing(server, seller);
                    break;
                case 3: display(server);
                    break;
                case 4:
                    System.exit(0);
                    break;
                default: System.out.println("invalid choice");
            }
        }
    }

    /**
     * This method allows the seller to create listings
     * The method calls a server method to store the listing
     * @param server Server which client is connected to
     * @param seller Instance of the seller making the listing
     */
    public void createListing(IServer server, Client seller) throws RemoteException {
        System.out.println("Enter item description\n");
        scn.nextLine();
        String description = scn.nextLine();
        System.out.println("Enter Starting Price");
        int startingPrice = scn.nextInt();
        System.out.println("Enter reserve price");
        int reservePrice = scn.nextInt();
        if(reservePrice <= startingPrice)
        {
            System.out.println("Error: Reserve price must be higher than starting price\n");
            return;
        }
        server.createListing(auctionId++, description, startingPrice, reservePrice, seller);
    }

    /**
     * This method allows the seller to close a listing, by calling a server method
     * @param server Server the seller is connected to
     * @param seller Instance of the seller closing the listing
     */
    public void closeListing(IServer server, Client seller) throws RemoteException{
        System.out.println("Enter auction ID of the listing you wish to close:\n");
        int auctionId = scn.nextInt();
        System.out.println(server.closeListing(auctionId, seller));
    }

    /**
     * This method calls on the server to return and print a list of the items currently up for for auction
     * @param server Server client is connected to
     */
    public void display(IServer server) throws RemoteException {
        System.out.println(server.displayListings());
    }
}

