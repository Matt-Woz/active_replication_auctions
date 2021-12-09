package backend;

import client.Client;

import java.io.Serializable;

/**
 * This class provides the AuctionItem object which represent the items which are put up for listing
 */
public class AuctionItem implements Serializable {
    private Client highestBidder;
    private final Client seller;
    private final String itemDescription;
    private int AuctionID;
    private final int startingPrice;
    private final int reservePrice;
    private int highestBid = 0;

    /**
     * This is the constructor of an AuctionItem
     * @param itemDescription Description of the item for the buyers
     * @param startingPrice Starting auction price of the item
     * @param reservePrice Minimum price that the item will sell for - Must be higher than starting price
     * @param seller Instance of the seller that is creating the auction - Used for authentication
     */
    public AuctionItem(int AuctionID, String itemDescription, int startingPrice, int reservePrice, Client seller) {
        this.AuctionID = AuctionID;
        this.itemDescription = itemDescription;
        this.startingPrice = startingPrice;
        this.reservePrice = reservePrice;
        this.seller = seller;
    }

    /**
     * Overridden toString() method
     * @return ItemID, item description, starting price, and current highest bid
     */
    @Override
    public String toString()
    {
        return "AuctionItem{" +
                "auctionID=" + AuctionID +
                " itemDescription='" + itemDescription + '\'' +
                ", startingPrice=" + startingPrice +
                ", currentPrice=" + highestBid +
                "\n}";
    }

    /**
     * Returns instance of the seller which created the auctionItem
     * @return Seller instance
     */
    public Client getSeller() {
        return seller;
    }

    /**
     * Returns the current highest bidder, used for determining who will win the auction
     * @return Highest bidder of item
     */
    public Client getHighestBidder() {
        return highestBidder;
    }

    /**
     * Used for setting new highest bidder when a new bid is made
     * @param highestBidder Instance of the buyer making the bid
     */
    public void setHighestBidder(Client highestBidder) {
        this.highestBidder = highestBidder;
    }

    /**
     * Returns the auctionId for the item
     * @return auctionId
     */
    public int getAuctionId() {
        return AuctionID;
    }

    /**
     * Returns the starting price of the item
     * @return Starting auction price of item
     */
    public int getStartingPrice() {
        return startingPrice;
    }

    /**
     * Returns the value of the current highest bid
     * @return Value of current highest bid on the item
     */
    public int getHighestBid() {
        return highestBid;
    }

    /**
     * Sets the highest bid when a new bid is made on the item
     * @param highestBid Value of the bid
     */
    public void setHighestBid(int highestBid) {
        this.highestBid = highestBid;
    }

    /**
     * Returns the value of the reserve price
     * @return Value of reserve price
     */
    public int getReservePrice() {
        return reservePrice;
    }
}

