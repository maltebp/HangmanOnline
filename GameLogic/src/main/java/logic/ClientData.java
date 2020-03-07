package logic;

public class ClientData {

    private long id;
    private String username;
    private String firstname;
    private String lastname;

    private GameState gameState;
    private String word;
    private int ratingChange;
    private int currentRating;

    public ClientData(long id, String username, String firstname, String lastname, int rating){
        this.id = id;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.currentRating = rating;
    }

    public long getId() {
        return id;
    }

    public void setGameState(GameState gameState){
        this.gameState = gameState;
    }


    public String getUsername() {
        return username;
    }
}
