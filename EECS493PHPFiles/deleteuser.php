<?php
require_once 'config.php';

if (file_get_contents('php://input')) {
	$json = file_get_contents('php://input');
	$obj = json_decode($json);

	// post request received, initialze variables
	$username = $obj->{'username'};
	$password = $obj->{'password'};

	$conn = mysqli_connect(DB_Hostname, DB_User, DB_Password, DB_Database);

	$query = "Delete from findme.Users where username = '".$username."' and password = '".$password."';";

	if (mysqli_query($conn, $query)) {
		http_response_code(200);
	}
	else {
		http_response_code(400);
	}
}
else {
	// get requrest received
	// do nothing
	http_response_code(404);
}

?>