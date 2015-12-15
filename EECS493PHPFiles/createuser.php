<?php
require_once 'config.php';

if (file_get_contents('php://input')) {
	$json = file_get_contents('php://input');
	$obj = json_decode($json);

	// post request received, initialze variables
	$username = $obj->{'username'};
	$password = $obj->{'password'};
	$color = $obj->{'color'};

	$conn = mysqli_connect(DB_Hostname, DB_User, DB_Password, DB_Database);

	$query = "Select username from findme.Users where username = '".$username."';";

	$result = mysqli_query($conn, $query);
	if ($row = mysqli_fetch_array($result)) {
		http_response_code(401);
	}
	else {
		$query = "Insert into findme.Users (username,password,color) value ('".$username."','".$password."',".$color.");";

		if (mysqli_query($conn, $query)) {
			http_response_code(200);
		}
		else {
			http_response_code(400);
		}
	}
}
else {
	// get requrest received
	// do nothing
	http_response_code(404);
}

?>