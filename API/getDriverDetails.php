<?php

include("connection.php");

$did = $_POST['did'];
// $did = "1";
  
$sql = "SELECT * FROM tbl_driver INNER JOIN tbl_vehicle_type ON tbl_driver.vehicle_type = tbl_vehicle_type.type Where tbl_driver.id = '$did' ";
// echo $sql;
$res = mysqli_query($con,$sql);
if(mysqli_num_rows($res) > 0)
{
	while($row = mysqli_fetch_assoc($res))
	{
		$data["data"][] = $row;
		// $data["data"][] = array('id' => $row['id'], 'type' => $row['type'], 'capacity' => $row['capacity'], 'rate' => ($distance*$row['rate'])+50, 'image' => $row['image']);
	}

	echo json_encode($data);
	
}
else
{
	echo "failed";
}

?>