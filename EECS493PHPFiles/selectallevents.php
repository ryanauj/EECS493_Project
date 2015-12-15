<?php
require_once 'config.php';

$conn = mysqli_connect(DB_Hostname, DB_User, DB_Password, DB_Database);

$result = mysqli_query($conn, SELECT_ALL);

$none = true;

$array = array();

while ($row = mysqli_fetch_array($result)) {
	$none = false;
	$arr = array('id' => $row[0], 'title' => $row[1], 'creator' => $row[2], 'date' => $row[3], 'description' => $row[4], 'lat' => $row[5], 'lng' => $row[6], 'color' => $row[7], 'endDate' => $row[8]);
	array_push($array, $arr);
}

if ($none == true) {
	http_response_code(404);
}
else {
	http_response_code(200);
	echo json_encode($array);
}

mysqli_close($conn);
?>