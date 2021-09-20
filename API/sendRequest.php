<?php

include("connection.php");

date_default_timezone_set("Asia/kolkata");
 
$uid = $_POST['uid'];
$startLoc = $_POST['startLoc'];
$destLoc = $_POST['destLoc'];
$startlat = $_POST['startlat'];
$startlon = $_POST['startlon'];
$destlat = $_POST['destlat'];
$destlon = $_POST['destlon'];
$payment = $_POST['payment'];
$vehicleType = $_POST['vehicleType'];

$date = date("m-d-Y");

/*$selectDriver = "SELECT id, name, latitude, longitude, SQRT( POW(69.1 * (latitude - '$startlat'), 2) + POW(69.1 * ('$startlon' - longitude) * COS(latitude / 57.3), 2)) AS distance FROM tbl_driver 
		WHERE ride_status = 'waiting' AND driver_status = 'Active' AND vehicle_type = '$vehicleType' HAVING distance < 5 ORDER BY distance DESC ";*/

		//echo $selectDriver;

// $res = mysqli_query($con,$selectDriver);

// if(mysqli_num_rows($res) > 0)
// {
	// $row = mysqli_fetch_assoc($res);
	// $data["data"][] = $row;
	// $did = $row['id'];

	$sql = "INSERT INTO tbl_request(uid, start_location, dest_location, start_lat, start_lon, dest_lat, dest_lon, trip_date, payment, trip_status, req_status, vehicle_type) VALUES ('$uid', '$startLoc', '$destLoc', '$startlat', '$startlon', '$destlat', '$destlon', '$date', '$payment', 'waiting', 'Requested', '$vehicleType') ";
 
	if(mysqli_query($con,$sql))
	{
		$insertId = mysqli_insert_id($con);
		$data["da"][] =  array('rid' => $insertId);
		// echo json_encode($data);
	echo json_encode($data);
		
	}
	else{
		echo "failed";
	}


// }
// else
// {
	// echo "No Driver Available Near";
// }

?>