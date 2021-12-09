package client;

import api.IServer;

import javax.crypto.*;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.UUID;

/**
 * This class provides the first menu a client will encounter, allowing them to register and log in
 * It also contains methods used for cryptographic authentication using a symmetric challenge-response protocol
 */
public class Authentication {
    private final Scanner scn = new Scanner(System.in);

    /**
     * This method provides the main menu before a user is logged in
     * @param server Server interface which client is connected to
     */
    public void menu(IServer server) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, ClassNotFoundException {
        while(true)
        {
            System.out.println("Main menu: ");
            System.out.println("1\t Log in");
            System.out.println("2\t Register");
            System.out.println("3\t Exit");
            int option = scn.nextInt();
            switch (option)
            {
                case 1:
                    logIn(server);
                    break;
                case 2:
                    System.out.println(registerAccount(server));
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
     * This method provides the interface for logging in once a user has a registered account
     * The method also calls the methods required to authenticate the server and client
     * @param server Server interface which client is connected to
     */
    public void logIn(IServer server) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, ClassNotFoundException {
        scn.nextLine();
        System.out.println("Please enter your email");
        String email = scn.nextLine();
        System.out.println("Please enter your password");
        String password = scn.nextLine();
        Client user = server.getUser(email);
        if(user == null)
        {
            System.out.println("Error - email not registered - please register");
            return;
        }
        if(!password.equals(user.getPassword()))
        {
            System.out.println("Error - password does not match account email");
            return;
        }
        if(user.isSeller())
        {
            sellerMenu sellermenu = new sellerMenu();
            sellermenu.menu(server, user);
        }
        else if(!user.isSeller())
        {
            buyerMenu buyermenu = new buyerMenu();
            buyermenu.menu(server, user);
        }
        else
        {
            System.out.println("Authentication error");
        }
    }

    /**
     * This method provides the interface for registering on the client side, and calls a method to store the client details
     * on the server.
     * The method also calls the required methods for authenticating the serer and client
     * @param server Server which client is connected to
     * @return Registration success or error message
     */
    public String registerAccount(IServer server) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, ClassNotFoundException {
        boolean isSeller;
        scn.nextLine();
        System.out.println("Enter your name");
        String name = scn.nextLine();
        System.out.println("Enter your e-mail address");
        String email = scn.nextLine();
        System.out.println("Please create a password");
        String password = scn.nextLine();
        System.out.println("Do you want a buyer or seller account? Enter b for buyer, s for seller");
        String buyerOrSeller = scn.nextLine();
        if(buyerOrSeller.equalsIgnoreCase("s"))
        {
            isSeller = true;
        }
        else if(buyerOrSeller.equalsIgnoreCase("b"))
        {
            isSeller = false;
        }
        else {
            return "Error - Please enter only s or b";

        }
        Client newUser = new Client(name, email, password, isSeller);
        return server.registerUser(newUser);
    }









}

