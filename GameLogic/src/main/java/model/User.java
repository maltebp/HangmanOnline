package model;


/** Represents user information
 *
 *  Used as DTO for SOAP
 * */
public class User {

    public String username;
    public String firstname;
    public String lastname;
    public int rating;

    @Override
    public String toString(){
        return String.format("Client( username: %s, firstname: %s, lastname: %s, rating: %d)",
            username,
            firstname,
            lastname,
            rating
        );
    }
}

