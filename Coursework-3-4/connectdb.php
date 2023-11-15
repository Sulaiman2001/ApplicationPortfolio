<?php
//connect to database
$db_host = 'localhost';
$db_name = 'u_200108120_db';
$username = 'u-200108120';
$password = 'Agtj8n0fsBKiG2T';

try {
	$conn = new PDO("mysql:dbname=$db_name;host=$db_host", $username, $password);
	$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch(PDOException $ex) {
	echo("Failed to connect to the database.<br>");
	echo($ex->getMessage());
	exit;
}
?>
