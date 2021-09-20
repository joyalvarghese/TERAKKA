<?php

include("connection.php");

$phone_number = $_POST['phone_number'];

$otp = rand (1000 , 9999);

$sql = "SELECT * FROM tbl_user WHERE phone_number='$phone_number'";
$res = mysqli_query($con,$sql);
if(mysqli_num_rows($res) > 0)
{
	$row = mysqli_fetch_assoc($res);
	echo '{ "id" : "'.$row['id'].'", "status" : "login", "otp" : "'.$otp.'" }';
	// echo "Number Already Exist";
}
else
{
	$sql = "INSERT INTO tbl_user (phone_number) VALUES ('$phone_number')";
	if(mysqli_query($con,$sql))
	{
		echo '{ "id" : "'.mysqli_insert_id($con).'", "status" : "registered", "otp" : "'.$otp.'" }';
	}
	else
		echo "failed";

}

?>