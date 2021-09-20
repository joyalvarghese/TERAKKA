<?php

include("connection.php");

$startlat = $_POST['startlat'];
$startlon = $_POST['startlon'];
$vehicleType = $_POST['vehicleType'];

$sql = "SELECT id, name, latitude, longitude, SQRT( POW(69.1 * (latitude - '$startlat'), 2) + POW(69.1 * ('$startlon' - longitude) * COS(latitude / 57.3), 2)) AS distance FROM tbl_driver WHERE vehicle_type = '$vehicleType' HAVING distance < 5 ORDER BY distance DESC ";

$res = mysqli_query($con,$sql);
if(mysqli_num_rows($res) > 0)
{
	while($row = mysqli_fetch_assoc($res))
	{
		$data["data"][] = $row;
	}

	echo json_encode($data);
	
}
else
{
	echo "failed";
}

?>