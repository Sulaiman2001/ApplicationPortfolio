<?php

//start the session, check if the user is not logged in, redirect to start
session_start();

function Welcome(){
	$email=$_SESSION['email'];
	echo "Welcome ".$_SESSION['email']."! ";
}

// Sorts events in ascending Order

$asc_query = "SELECT * FROM event ORDER BY event_time ASC";
$result = executeQuery($asc_query);





function executeQuery($query)
{
	$connect = mysqli_connect("localhost", "u-200108120", "Agtj8n0fsBKiG2T", "u_200108120_db");
	$result = mysqli_query($connect, $query);
	return $result;
}

?>

<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8" />
	<title>Homepage</title>
	<!--Calls external files-->
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
	<br><div class = "content">

		<table style = "margin-left: auto; margin-right: auto;">
			<tr><th align='center'><b></th>   <th align='center'><b></b></th> <th align='center'><b></b></th ></tr>
				<!-- display data from database -->
				<?php while ($row = mysqli_fetch_array($result)):
					echo "<tr>
					<td>"?><img src="<?php echo $row["imageurl"];?>" height="300" width= "450"><?php echo "</td>
					";
					echo  "
					<td>" ."Event name: " . $row['event_name'].
					"<br>Event category: " . $row['event_category'] .
					"<br>Event time: " . $row['event_time'] ."</td>
					";
					?>
					<td><button>
						<a href="<?php echo $row['moreInfoLink'];?>"><p style="color:white">Click to find out more information</p></a>
					</button></td></tr>
				<?php endwhile;?>
			</table>
		</form>
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
