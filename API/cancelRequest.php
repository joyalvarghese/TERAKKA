<?php

include("connection.php");
  
$rid = $_POST['rid'];
// $uid = "1";


$date = date("d-m-Y");
$time = date("h:i:sa");
$sql = "UPDATE tbl_request SET trip_status = 'canceled', req_status = 'canceled', trip_amount = '0', trip_date = '$date', start_time = '$time' Where id = '$rid'";
if(mysqli_query($con,$sql))
{
	echo "canceled";
	
}
else
{
	echo "failed";
}

?>