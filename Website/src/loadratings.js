
// Load the User information via the Database API
function loadRatings(){
    console.log("Loading ratings!");

    $.ajax({
        type: "get",
        url: "http://maltebp.dk:45786/user/all",
        contentType: "application/json",

        success: (data) => {
            // Load and add users to rating board
            users = JSON.parse(data).users;
            users.forEach(user => {
                console.log(user);
                addUserRatingRow(user);
            });

            // Hide loader and show table
            document.getElementById("loadingspinner").style.display = "none";
            document.getElementById("ratingtable").style.display = "inline";            
        },

        error: (data) => {
            document.getElementById("loadingspinner").style.display = "none";
            document.getElementById("rating_error").style.display = "block";
            console.log("Error when loading user ratings: ");
            console.log(data);
        }
    })

}

// Adds a user to the rating board
function addUserRatingRow(user){
    var row = document.createElement("tr");
    row.classList.add("ratingtable_row");

    var cellID = document.createElement("th");
    cellID.classList.add("ratingtable_id")
    cellID.textContent = user.username;
    row.appendChild(cellID);

    var cellUsername = document.createElement("th");
    cellUsername.classList.add("ratingtable_name")
    cellUsername.textContent = user.firstname;
    
    row.appendChild(cellUsername);

    var cellRating = document.createElement("th");
    cellRating.classList.add("ratingtable_rating")
    cellRating.textContent = user.rating;
    row.appendChild(cellRating);

    var cellFiller = document.createElement("th");
    cellFiller.classList.add("ratingtable_id")
    row.appendChild(cellFiller);

    var table = document.getElementById("ratingtable");
    table.appendChild(row);
}

// Hide table and error message
document.getElementById("ratingtable").style.display = "none";
document.getElementById("rating_error").style.display = "none";

// Start loading of ratings
loadRatings();
