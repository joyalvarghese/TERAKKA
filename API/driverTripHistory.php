<?php

include("connection.php");

// $uid = $_POST['uid'];
$uid = "1";

$date = date("d-m-Y");
$time = time("h:i:s"); 
 
 
$sql = " SELECT * FROM tbl_request WHERE did = '$uid' AND (trip_date <= '$date' OR CAST(end_time as time) <= '$time') ";
$result = mysqli_query($con,$sql);
if(mysqli_num_rows($result) > 0)
{
	while ($row = mysqli_fetch_assoc($result)) {

		// echo $row['did'];

		if($row['did'] != 0){

			$sql = " SELECT * FROM tbl_request INNER JOIN tbl_user ON tbl_request.uid = tbl_user.id WHERE tbl_request.did = '$uid' AND (tbl_request.trip_date <= '$date' OR CAST(tbl_request.end_time as time) <= '$time') ";

			// echo $sql;
			$result = mysqli_query($con,$sql);
			if(mysqli_num_rows($result) > 0)
			{
				while ($row = mysqli_fetch_assoc($result)) {
					// $dsql = "";
					$data["da"][] = $row;
				}

				echo json_encode($data);
			}
			else
			{
				echo "failed";
			}

		}
		else{
			echo "failed";
		}
	}

}
else
	echo "failed";



?>