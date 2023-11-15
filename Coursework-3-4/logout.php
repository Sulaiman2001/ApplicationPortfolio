<?php

	session_start();
	unset($_SESSION["email"]);
	//session_destroy();

?>
 <H2> Logged out now! </H2>
 <p>Would like to log in again? <a href="index.php">Log in</a>  </p>
