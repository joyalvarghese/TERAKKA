<?php

include("connection.php");

$latitude = $_POST['latitude'];
$longitude = $_POST['longitude'];
$uid = $_POST['uid'];

$sql = "UPDATE tbl_driver SET latitude = '$latitude', longitude = '$longitude'  WHERE id = '$uid' ";
if(mysqli_query($con,$sql))
{
	echo "updated";
}
else
{
	echo "failed";
}

?>