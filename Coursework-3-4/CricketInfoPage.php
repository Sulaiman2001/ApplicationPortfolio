<?php
session_start();
//checks if the user logged in if they are not takes them to the index page

function Welcome(){//welcome the user function
  $email=$_SESSION['email'];
  echo "Welcome ".$_SESSION['email']."! ";
}
require_once ('connectdb.php');
// selects the data about cricket from the database
try {
  $query="SELECT  * FROM  event WHERE event_name='Cricket'";
  //run  the query
  $rows =  $conn->query($query);

  //step 3: display the course list in a table
  if ( $rows && $rows->rowCount()> 0) {


    ?>
    <!DOCTYPE html>
    <html lang="en">
    <head>
      <meta charset="utf-8" />
      <title>CricketEventPage</title>
      <link href="css/InformationPage.css" rel="stylesheet" />
    </head>
    <body>
      <!--nav bar-->
      <div class="navbar">
        <a href="Homepage.php">Home</a>
        <a href="logout.php">Log out</a>

        <div class="dropdown">
          <button class="dropbtn">Sort/Filter
          </button>
          <div class="dropdown-content">
            <a href="SortASC.php">Sort(Ascending)</a>
    				<a href="SortDEC.php">Sort(Descending)</a>
    				<a href="SortSport.php">Sort(Sport)</a>
    				<a href="SortOther.php">Sort(Other)</a>
    				<a href="SortCulture.php">Sort(Culture)</a>
          </div>
        </div>
      </div>

      <div class="content">
        <div class="column">
          <!--Slideshow-->
          <div class="slideshow-container">

            <div class="mySlides fade">
              <div class="numbertext">1 / 3</div>
              <img src="images/cricketpic1.jpeg" style="width:100%" height="550">
            </div>

            <div class="mySlides fade">
              <div class="numbertext">2 / 3</div>
              <img src="images/cricketpic2.jpeg" style="width:100%" height="550">
            </div>

            <div class="mySlides fade">
              <div class="numbertext">3 / 3</div>
              <img src="images/cricketpic3.webp" style="width:100%" height="550">
            </div>

          </div>
          <br>

          <div style="text-align:center">
            <span class="dot"></span>
            <span class="dot"></span>
            <span class="dot"></span>
          </div>
          <h1> Cricket event</h1>
          <h2><?php echo "Welcome ".$_SESSION['email']."! ";?>
          </h2>

          <form method = "post" action="CricketInfoPage.php">
            <!--Like button-->
            <input type="submit" name="like" value="Like"/>
            <?php
            if (isset($_POST['like'])){
              $db_host = 'localhost';
              $db_name = 'u_200108120_db';
              $username = 'u-200108120';
              $password = 'Agtj8n0fsBKiG2T';
              $_POST=$_SESSION['email'];
              // Create connection
              $conn = new mysqli($db_host, $username, $password, $db_name);
              // Check connection
              if ($conn->connect_error) {
                die("Connection failed: " . $conn->connect_error);
              }
              $sql = "INSERT INTO likes (email, event, likes)
              VALUES ('$_POST', '3', '1')";//inserts data with the users email, this event and sets the likes to true

              if ($conn->query($sql) === TRUE) {
                ?><script> alert('You have liked this event')</script><?php//alert displaying the input was successful
              } else {
                echo "Error: " . $sql . "<br>" . $conn->error;
              }


              $conn->close();
            }

            ?>
            <!--Register button-->
            <input type= "Submit" name = "Submit"  value = "Register"/><br><br><br>
            <?php
            if (isset($_POST['Submit'])){
              $db_host = 'localhost';
              $db_name = 'u_200108120_db';
              $username = 'u-200108120';
              $password = 'Agtj8n0fsBKiG2T';
              $_POST=$_SESSION['email'];
              // Create connection
              $conn = new mysqli($db_host, $username, $password, $db_name);
              // Check connection
              if ($conn->connect_error) {
                die("Connection failed: " . $conn->connect_error);
              }

              $sql = "INSERT INTO register (email, event)
              VALUES ('$_POST', '3')";//inserts data to the database with the users email and this particular event

              if ($conn->query($sql) === TRUE) {
                ?><script> alert('You have registered for this event')</script><?php//alert displaying the input was successful
              } else {
                echo "Error: " . $sql . "<br>" . $conn->error;
              }


              $conn->close();
            }
            ?>
            <!--Remove like button-->
            <input type="submit" name="unlike" value="Remove Like"/>
            <?php
            if (isset($_POST['unlike'])){
              $db_host = 'localhost';
              $db_name = 'u_200108120_db';
              $username = 'u-200108120';
              $password = 'Agtj8n0fsBKiG2T';
              $_POST=$_SESSION['email'];
              // Create connection
              $conn = new mysqli($db_host, $username, $password, $db_name);
              // Check connection
              if ($conn->connect_error) {
                die("Connection failed: " . $conn->connect_error);
              }
              $sql = "DELETE FROM likes WHERE email='$_POST' AND event = '3'";//deletes any like with this particular data


              if ($conn->query($sql) === TRUE) {
                ?><script> alert('There are no likes recorded for your account')</script><?php
              } else {
                echo "Error: " . $sql . "<br>" . $conn->error;
              }


              $conn->close();
            }

            ?>
            <!--Remove registration button-->
            <input type="submit" name="delete" value="Remove registeration"/>
            <?php
            if (isset($_POST['delete'])){
              $db_host = 'localhost';
              $db_name = 'u_200108120_db';
              $username = 'u-200108120';
              $password = 'Agtj8n0fsBKiG2T';
              $_POST=$_SESSION['email'];
              // Create connection
              $conn = new mysqli($db_host, $username, $password, $db_name);
              // Check connection
              if ($conn->connect_error) {
                die("Connection failed: " . $conn->connect_error);
              }
              $sql = "DELETE FROM register WHERE email='$_POST' AND event = '3'";//deletes the records with this data


              if ($conn->query($sql) === TRUE) {
                ?><script> alert('There are no registrations recorded for your account')</script><?php//alert displaying the input was successful
              } else {
                echo "Error: " . $sql . "<br>" . $conn->error;
              }


              $conn->close();
            }

            ?>
            <input type="hidden" name="submitted" value="true"/>
          </form>
          <!--Display data about the event-->
          <table style = "margin-left: auto; margin-right: auto;">
            <tr><th align='center'><b></th>   <th align='center'><b></b></th> <th align='center'><b></b></th ></tr>
              <?php
              // Fetch and  print all  data
              while  ($row =  $rows->fetch())	{
                echo  "<tr>
                <td>" ."<u><b>Event name:</u></b> " . $row['event_name'].
                "<br><br><u><b>Event category:</u></b> " . $row['event_category'] .
                "<br><br><u><b>Event description:</u></b> " . $row['event_description'] .
                "<br><br><u><b>Event organiser:</u></b> " . $row['organiser'] .
                "<br><br><u><b>Location:</u></b> " . $row['place'] .
                "<br><br><u><b>Event time:</u></b> " . $row['event_time'] ."</td>
                </tr>";
              }
              echo  '</table>';
            }
            else {
              echo  "<p>No  course in the list.</p>\n"; //no match found
            }
          }
          catch (PDOexception $ex){
            echo "Sorry, a database error occurred! <br>";
            echo "Error details: <em>". $ex->getMessage()."</em>";
          }

          ?>
        </div>
        <!--Slideshow-->
        <script>
        var slideIndex = 0;
        showSlides();

        function showSlides() {
          var i;
          var slides = document.getElementsByClassName("mySlides");
          var dots = document.getElementsByClassName("dot");
          for (i = 0; i < slides.length; i++) {//slides length
            slides[i].style.display = "none";
          }
          slideIndex++;
          if (slideIndex > slides.length) {slideIndex = 1}
          for (i = 0; i < dots.length; i++) {//increment dots by 1
            dots[i].className = dots[i].className.replace(" active", "");
          }
          slides[slideIndex-1].style.display = "block";
          dots[slideIndex-1].className += " active";
          setTimeout(showSlides, 4000);//time to change slide
        }
        </script>

        </html>
