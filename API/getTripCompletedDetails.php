<?php

include("connection.php");

date_default_timezone_set("Asia/kolkata");

$did = $_POST['did'];
$rid = $_POST['rid'];
// $did = "1";
// $rid = "1";
$d = date("h:i:sa");

$date = date("m-d-Y");
 
$sql = "SELECT * FROM tbl_request WHERE id = '$rid' ";
$res = mysqli_query($con,$sql);
$row = mysqli_fetch_assoc($res);

$newDate = date("F j, Y", strtotime($date));

$dsql = "SELECT * FROM tbl_driver WHERE id = '$did'";
$dres = mysqli_query($con,$dsql);
$drow = mysqli_fetch_assoc($dres);

$data["data"][] = array('price' => $row['trip_amount'], 'date_time' => $newDate." at ".$row['end_time'], 'name' => $drow['name'], 'image' => $drow['driver_image']);

echo json_encode($data);

?>