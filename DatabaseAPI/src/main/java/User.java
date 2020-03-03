import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

public class User implements Comparable<User> {

    private String firstname;
    private String lastname;
    private String username;
    private int rating = 0;


    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public String toString(){
        return String.format("User{ username: %s, name: %s, %s, rating: %d}",
                username, firstname, lastname, rating );
    }

    /** Convert User information to JSON string */
    public String toJSON() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }

    @Override
    public int compareTo(@NotNull User user) {
        // Sort highest rated first
        return user.rating-rating;
    }
}
