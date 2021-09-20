<?php

include("connection.php");

$phone_number = $_POST['phone_number'];

$otp = rand (1000 , 9999);

$sql = "SELECT * FROM tbl_driver WHERE phone ='$phone_number'";
// echo $sql;
$res = mysqli_query($con,$sql);
if(mysqli_num_rows($res) > 0)
{
	$row = mysqli_fetch_assoc($res);
	echo '{ "id" : "'.$row['id'].'", "status" : "login", "otp" : "'.$otp.'" }';
	// echo "Number Already Exist";
}
else
{
	echo "failed";
}

?>