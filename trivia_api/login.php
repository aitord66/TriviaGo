<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");

require_once "db.php";

$data = json_decode(file_get_contents("php://input"), true);

$username = trim($data["username"] ?? "");
$password = trim($data["password"] ?? "");

if (!$username || !$password) {
    echo json_encode(["success" => false, "message" => "Faltan campos"]);
    exit;
}

$stmt = $pdo->prepare("SELECT id_user, password FROM usuarios WHERE username = ?");
$stmt->execute([$username]);
$row = $stmt->fetch(PDO::FETCH_ASSOC);

if ($row && password_verify($password, $row["password"])) {
    echo json_encode(["success" => true, "id_user" => $row["id_user"]]);
} else {
    echo json_encode(["success" => false, "message" => "Credenciales incorrectas"]);
}
