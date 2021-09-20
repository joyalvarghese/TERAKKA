<?php

include("connection.php");
 
$uid = $_POST['uid'];
$rid = $_POST['rid'];

$usql = "UPDATE tbl_request SET req_status = 'Accepted', did = '$uid' WHERE id = '$rid'";
mysqli_query($con,$usql);

?>