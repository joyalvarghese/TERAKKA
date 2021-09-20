<?php

include("connection.php");

date_default_timezone_set("Asia/kolkata");

$uid = $_POST['uid'];
$rid = $_POST['rid'];
$d = date("h:i:sa");
// $uid = "1";

$sql = "UPDATE tbl_request SET start_time = '$d', trip_status = 'on_trip' Where id = '$rid' ";
echo $sql;
mysqli_query($con,$sql);

?>