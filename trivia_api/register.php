<?php
// =============================================================
//  register.php  –  Registrar nuevo usuario
//  Subir a tu servidor PHP junto al resto de archivos de la API
// =============================================================
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");

require_once "db.php";

$data = json_decode(file_get_contents("php://input"), true);

$username = trim($data["username"] ?? "");
$password = trim($data["password"] ?? "");
$email    = trim($data["email"]    ?? "");

if (!$username || !$password || !$email) {
    echo json_encode(["success" => false, "message" => "Faltan campos"]);
    exit;
}

// Comprobar si ya existe
$stmt = $pdo->prepare("SELECT id_user FROM usuarios WHERE username = ?");
$stmt->execute([$username]);
if ($stmt->fetch()) {
    echo json_encode(["success" => false, "message" => "El usuario ya existe"]);
    exit;
}

$hash = password_hash($password, PASSWORD_BCRYPT);
$stmt = $pdo->prepare("INSERT INTO usuarios (username, password, email) VALUES (?, ?, ?)");
$ok = $stmt->execute([$username, $hash, $email]);

echo json_encode(["success" => $ok]);
