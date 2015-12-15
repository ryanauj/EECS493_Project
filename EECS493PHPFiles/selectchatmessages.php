<?php
require_once 'config.php';

if (file_get_contents('php://input')) {
	$json = file_get_contents('php://input');
	$obj = json_decode($json);

	// post request received, initialze variables\
	$eventid = $obj->{'eventid'};

	$conn = mysqli_connect(DB_Hostname, DB_User, DB_Password, DB_Database);

	$query = "Select username,date,message from findme.ChatBoard where eventid = ".$eventid.";";
	
	$none = true;

	$array = array();

	$result = mysqli_query($conn, $query);

	while ($row = mysqli_fetch_array($result)) {
		$none = false;
		$arr = array('username' => $row[0], 'date' => $row[1], 'message' => $row[2]);
		array_push($array, $arr);
	}

	if ($none == true) {
		http_response_code(404);
	}
	else {
		http_response_code(200);
		echo json_encode($array);
	}

}
else {
	// get requrest received
	// do nothing
	http_response_code(404);
}

?>