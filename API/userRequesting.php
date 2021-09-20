<?php

include("connection.php");
  
$rid = $_POST['rid'];
 
$sql = "SELECT * FROM tbl_request Where id = '$rid' AND req_status = 'Accepted' AND did != '0' ";
$res = mysqli_query($con,$sql);
if(mysqli_num_rows($res) > 0)
{
	$row = mysqli_fetch_assoc($res);
	$data["da"][] =  array('rid' => $rid,'did' => $row['did']);
	echo json_encode($data);
	
}
else
{
	echo "failed";
}

?>