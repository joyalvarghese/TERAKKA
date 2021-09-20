<?php

include("connection.php");

$slat = $_POST['slat'];
$slon = $_POST['slon'];
$dlat = $_POST['dlat'];
$dlon = $_POST['dlon'];

$distance = round(SQRT( POW(69.1 * ($dlat - $slat), 2) + POW(69.1 * ($slon - $dlon) * COS($dlat / 57.3), 2)),2);

// echo $distance;
 
$sql = "SELECT * FROM tbl_vehicle_type";
$res = mysqli_query($con,$sql);
if(mysqli_num_rows($res) > 0)
{
	while($row = mysqli_fetch_assoc($res))
	{
		// $data["data"][] = $row;
		$data["data"][] = array('id' => $row['id'], 'type' => $row['type'], 'capacity' => $row['capacity'], 'rate' => ($distance*$row['rate'])+50, 'image' => $row['image']);
	}

	echo json_encode($data);
	
}
else
{
	echo "failed";
}

?>