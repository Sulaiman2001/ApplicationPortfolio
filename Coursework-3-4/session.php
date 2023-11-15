<?php
	//start the session, check if the user is not logged in, redirect to index
	session_start();

	if (!isset($_SESSION['email'])){
		header("Location: index.php");
		exit();
	}
	function Welcome(){
	$email=$_SESSION['email'];
	echo "Welcome ".$_SESSION['email']."! ";
}
	require_once ('connectdb.php');
?>
