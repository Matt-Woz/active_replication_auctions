package client;

import java.io.Serializable;

/**
 * This class provides the Client object which represents the users of the auction system
 */
public class Client implements Serializable {
    private final String name;
    private final String email;
    private final String password;
    private final boolean isSeller;

    /**
     * This is the constructor of the Client class
     * @param name Name of the user
     * @param email Email of the user
     * @param password The user's password
     * @param isSeller true if user is a seller account, false if buyer
     */
    public Client(String name, String email, String password, boolean isSeller)
    {
        this.name = name;
        this.email = email;
        this.password = password;
        this.isSeller = isSeller;
    }

    /**
     * Determines if client is a seller
     * @return true if seller, false if buyer
     */
    public boolean isSeller() {
        return isSeller;
    }

    /**
     * Gives the email of the client
     * @return Email registered to client
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gives name of the client
     * @return Name registered to client
     */
    public String getName() {
        return name;
    }

    /**
     * Gives password of client
     * @return password of client
     */
    public String getPassword() {
        return password;
    }

    /**
     * toString overridden method provides only the name and email of the user
     * @return Name and email of Client
     */
    @Override
    public String toString() {
        return "Client{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
