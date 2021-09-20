<?php

include("connection.php");

$user_id = $_POST['user_id'];
$first_name = $_POST['first_name'];
$last_name = $_POST['last_name'];
$email = $_POST['email'];
 
 
$sql = " UPDATE tbl_user SET first_name='$first_name', last_name='$last_name', email_id='$email' WHERE id = '$user_id' ";
if(mysqli_query($con,$sql))
{
	echo $sql;
	echo "updated";
}
else
{
	echo "failed";
}

?>