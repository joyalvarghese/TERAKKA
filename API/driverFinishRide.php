<?php

include("connection.php");

date_default_timezone_set("Asia/kolkata");

$uid = $_POST['uid'];
$rid = $_POST['rid'];
$d = date("h:i:sa");
 
$sql = "SELECT * FROM tbl_request WHERE id = '$rid' ";
$res = mysqli_query($con,$sql);
$row = mysqli_fetch_assoc($res);

$to_time = strtotime($d);
$from_time = strtotime($row['start_time']);
$time = round(abs($to_time - $from_time) / 60,2). " minute";

$price = $time * 5;

$date_format =  date("F j, Y")." at ".date("h:i A");

$sql = "UPDATE tbl_request SET end_time = '$d', trip_status = 'completed', req_status = 'completed', trip_amount = '$price' Where id = '$rid' ";
mysqli_query($con,$sql);

$sql = "SELECT * FROM tbl_user WHERE id ='$row[uid]' ";
$res = mysqli_query($con,$sql);
$row = mysqli_fetch_assoc($res);
// $data["data"][] = $row;
$data["data"][] = array('name' => $row['first_name']." ".$row['last_name'], 'price' => $price, 'date_time' => $date_format);

echo json_encode($data);

?>