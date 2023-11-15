<?php
//starts seession
session_start();

function Welcome(){
	$email=$_SESSION['email'];
	echo "Welcome ".$_SESSION['email']."! ";
}
//calls connection
require_once ('connectdb.php');
?>

<!DOCTYPE html>
<html lang="en">
<head>

	<meta charset="utf-8" />
	<title>Homepage</title>
	<!--Call external files-->
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
	<link href="css/HomepageLayout.css" rel="stylesheet" />
	<script src="js/validate_password.js"></script>
	<script src="js/main.js"></script>
</head>
<body>
	<div class ="header">
		<h1><?php echo "Welcome ".$_SESSION['email']."! ";?></h1>
	</div>
	<!--Nav bar-->

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
	<center>
		<br><div class = "content">

			<table>
				<tr><th align='center'><b></th>   <th align='center'><b></b></th> <th align='center'><b></b></th ></tr>
					<?php
					try {
						$query="SELECT  * FROM  event ";
						//run  the query
						$rows =  $conn->query($query);

						//display the events
						if ( $rows && $rows->rowCount()> 0) {

							// Fetch and  print all  the records.
							while  ($row =  $rows->fetch())	{

								echo "<tr>
								<td>"?><img src="<?php echo $row["imageurl"];?>" height="300" width= "450"><?php "</td>
								";
								echo  "
								<td>" ."Event name: " . $row['event_name'].
								"<br>Event category: " . $row['event_category'] .
								"<br>Event time: " . $row['event_time'] ."</td>
								";
								//button to find more info?>
								<td><button>
									<a href="<?php echo $row['moreInfoLink'];?>"><p style="color:white">Click to find out more information</p></a>
								</button></td></tr><?php
							}
							echo  '</table>';
						}
						else {
							echo  "<p>No course in the list.</p>\n"; //no match found
						}
					}
					catch (PDOexception $ex){
						echo "Sorry, a database error occurred! <br>";
						echo "Error details: <em>". $ex->getMessage()."</em>";
					}

					?>

				</div>
			</div>
			<!--Dropdown for sorting-->
			<script>
			function myFunction() {
				document.getElementById("myDropdown").classList.toggle("show");
			}

			// Close the dropdown if the user clicks outside of it
			window.onclick = function(event) {
				if (!event.target.matches('.dropbtn')) {
					var dropdowns = document.getElementsByClassName("dropdown-content");
					var i;
					for (i = 0; i < dropdowns.length; i++) {
						var openDropdown = dropdowns[i];
						if (openDropdown.classList.contains('show')) {
							openDropdown.classList.remove('show');
						}
					}
				}
			}
			</script>
			<center>
				<!--Social links-->

<footer>
  <a href="https://www.instagram.com/astonuniversity/?hl=en" class="fa fa-instagram"></a>
  <a href="https://twitter.com/AstonUniversity?ref_src=twsrc%5Egoogle%7Ctwcamp%5Eserp%7Ctwgr%5Eauthor" class="fa fa-twitter"></a>
  <a href="https://www.facebook.com/astonuniversity/" class="fa fa-facebook"></a>
</footer>
</center>
		</body>
		</html>
