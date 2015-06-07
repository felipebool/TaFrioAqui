<?php
	function insertIntoDatabase($name, $address, $lat, $lon, $type) {
		$servername = "localhost";
		$username = "felipe";
		$password = "xxx";
		$dbname = "tafrioaqui";

		try {
			$conn = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);
			$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

			$sql = "INSERT INTO localizacao (name, address, latitude, longitude, type)
                 VALUES ($name, $address, $lat, $lon, $type)";
			$conn->exec($sql);
		}
		catch(PDOException $e) {
		}

		$conn = null;
	}

	if ($_SERVER['REQUEST_METHOD'] == 'POST') {
		$json = file_get_contents('php://input');
		$obj = json_decode($json);
		header('Content-Type: application/json;');
		header('HTTP/1.1 200 OK');

		if (isset($obj->latitude) && isset($obj->longitude)) {
			$response = array("result" => "ok");
			$jsonResponse = json_encode($response);

			insertIntoDatabase($obj->name, $obj->address, $obj->latitude, $obj->longitude, $obj->type);
			echo $jsonResponse;
		}
		else {
			$response = array("result" => "fail");
			$jsonResponse = json_encode($response);
			echo $jsonResponse;
		}
	}
	exit; 
?>
    
