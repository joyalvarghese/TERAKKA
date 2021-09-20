<?php

include("connection.php");
  
$uid = $_POST['uid'];
// $uid = "1";

$sql = "SELECT * FROM tbl_request Where uid = '$uid' AND req_status = 'Accepted' OR req_status = 'Requested' AND trip_status = 'on_trip' OR trip_status = 'waiting'";
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