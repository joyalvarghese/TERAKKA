<?php

include("connection.php");
 
$uid = $_POST['uid'];
// $uid = "1";

$selectDriver = "SELECT * FROM tbl_driver WHERE id = '$uid' AND driver_status = 'Active' ";
$res = mysqli_query($con,$selectDriver);

if(mysqli_num_rows($res) > 0)
{
	$row = mysqli_fetch_assoc($res);
	$vehicleType = $row['vehicle_type'];
	$lat = $row['latitude'];
	$lon = $row['longitude'];


	// $data["data"][] = $row;
	$did = $row['id'];

	$sql = "SELECT id, uid, start_location, dest_location, start_time, trip_date, start_lat, start_lon, dest_lat, dest_lon, SQRT( POW(69.1 * (start_lat - '$lat'), 2) + POW(69.1 * ('$lon' - start_lon) * COS(start_lat / 57.3), 2)) AS distance FROM tbl_request WHERE req_status = 'Requested' AND trip_status = 'waiting' AND vehicle_type = '$vehicleType' HAVING distance < 5 ORDER BY distance DESC ";
 
 	// echo $sql."<br>";

 	$result = mysqli_query($con,$sql);
	if(mysqli_num_rows($result) > 0)
	{
		$row = mysqli_fetch_assoc($result);
		$id = $row['id'];
		$cid = $row['uid'];

		$csql = "SELECT * FROM tbl_user WHERE id = '$cid'";
		$cres = mysqli_query($con,$csql);
		$crow = mysqli_fetch_assoc($cres);

		$data["data"][] = array('id' => $row['id'], 'name' => $crow['first_name'], 'phone' => $crow['phone_number'], 'start_location' => $row['start_location'], 'dest_location' => $row['dest_location'], 'start_lat' => $row['start_lat'], 'start_lon' => $row['start_lon'], 'dest_lat' => $row['dest_lat'], 'dest_lon' => $row['dest_lon'], 'trip_date' => $row['trip_date'], 'start_time' => $row['start_time']); 

		/*$usql = "UPDATE tbl_request SET req_status = 'Accepted', did = '$uid' WHERE id = '$id'";
		mysqli_query($con,$usql);*/
		echo json_encode($data);
		
	}
	else{
		echo "failed";
	}

}
else
{
	echo "Driver Inactive";
}

?>