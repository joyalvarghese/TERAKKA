<?php

include("connection.php");
  
$uid = $_POST['uid'];
// $uid = "1";

$sql = "SELECT *,tbl_request.id as rid FROM tbl_request INNER JOIN tbl_user ON tbl_request.uid = tbl_user.id Where tbl_request.did = '$uid' AND tbl_request.req_status = 'Accepted' AND ( tbl_request.trip_status = 'on_trip' OR tbl_request.trip_status = 'waiting' ) ";

// echo $sql."<br>";

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