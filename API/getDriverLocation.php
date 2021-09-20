<?php

include("connection.php");
 
$did = $_POST['did']; 
$rid = $_POST['rid'];

// echo $did."  ".$rid;
   
$sql = "SELECT trip_status FROM tbl_request Where id = '$rid' ";
$res = mysqli_query($con,$sql);
$row = mysqli_fetch_assoc($res);
$trip_status = $row['trip_status'];


$sql = "SELECT latitude,longitude FROM tbl_driver Where id = '$did' ";
$res = mysqli_query($con,$sql);
if(mysqli_num_rows($res) > 0)
{
	$row = mysqli_fetch_assoc($res);
	// $data["data"][] = $row;
	$data["data"][] =  array('trip_status' => $trip_status, 'latitude' => $row['latitude'], 'longitude' => $row['longitude']);

	echo json_encode($data);
	
}
else
{
	echo "failed";
}

?>