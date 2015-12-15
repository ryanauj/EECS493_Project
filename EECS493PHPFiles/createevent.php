<?php
require_once 'config.php';

if (file_get_contents('php://input')) {
	$json = file_get_contents('php://input');
	$obj = json_decode($json);

	// post request received, initialze variables\
	$title = $obj->{'title'};
	$creator = $obj->{'creator'};
	$date = $obj->{'date'};
	$description = $obj->{'description'};
	$lat = $obj->{'lat'};
	$lng = $obj->{'lng'};
	$color = $obj->{'color'};
	$endDate = $obj->{'endDate'};

	$conn = mysqli_connect(DB_Hostname, DB_User, DB_Password, DB_Database);

	$query = "Insert into findme.Events (title,creator,date,description,lat,lng,color,endDate) values ('".$title."','".$creator."','".$date."','".$description."','".$lat."','".$lng."',".$color.",'".$endDate."');";
	
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