<?php
//if the form has been submitted
if (isset($_POST['submitted'])){


  // connect to the database
  require_once('connectdb.php');

  $email=isset($_POST['email'])?$_POST['email']:false;
  $phone=isset($_POST['phone'])?$_POST['phone']:false;
  $password=isset($_POST['password'])?password_hash($_POST['password'],PASSWORD_DEFAULT):false;//hash the password

  if (!($email)){
    echo "Email wrong!";
    exit;
  }
  if (!($password)){
    exit("password wrong!");
  }
  try{
    $stat=$conn->prepare("SELECT * FROM users WHERE email = ?");
    $stat->execute(array($_POST['email']));//searches for the users email in the database
    if ($stat->rowCount()>0){  // matching email
      ?><script> alert('Email already exists')</script><?php
    }else{
      //register user by inserting the user info(prevent sql injections)
      $stat=$conn->prepare("insert into users values(?,?,?)");
      $stat->execute(array($email, $password, $phone));

      $id=$conn->lastInsertId();
      session_start();
      $_SESSION["email"]=$_POST['email'];//takes user to homepage
      header("Location:Homepage.php");
      exit();
    }
  }
  catch (PDOexception $ex){
    echo "Sorry, a database error occurred! <br>";
    echo "Error details: <em>". $ex->getMessage()."</em>";
  }


}
?>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8" />
  <title>SignUp</title>
  <!--Call external files-->
  <link href="css/RegisterLayout.css" rel="stylesheet" />
  <script src="js/validate_password.js"></script>
  <script src="js/main.js"></script>
</head>
<body>
  <center>
    <div class="SignUp">
      <h2>Sign up</h2>
      <form method = "post" action="Register.php" id="sign-up">
        <h1>Please enter your Aston email address:</h1>
        <input type="text"
        placeholder="Email"
        name="email"
        required pattern=".+(@aston.ac.uk)"<?php//checks for an aston tag?>
        title="must be an Aston University email address."/>
        <h1>Please create a password:</h1>
        <input type="password"
        placeholder="Password"
        name="password"
        required />
        <input type="password"
        placeholder="Confirm Password"
        name="confirm_pass"
        required /><br>
        <h1>Please enter your mobile number</h1>
        <input type="text"
        placeholder="07xxxxxxxxxxx"
        name="phone"
        required pattern="[07]{2}[0-9]{9}"<?php//checks for an 11 digit number with 07 at the beginning?>
        title= "please enter an actual 11 digit mobile number"/><br>
        <input type="submit" value="Register" />
        <input type="hidden" name="submitted" value="true"/><br><br>
        <a class="link" href="index.php">Clik to return to sign in page</a>
      </form>
    </div>
  </center>
</body>
</html>
