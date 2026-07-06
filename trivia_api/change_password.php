<?php
// =============================================================
//  change_password.php  –  Cambiar la contraseña de un usuario
//  POST: { id_user, contrasenaActual, nuevaContrasena }
// =============================================================
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");

require_once "db.php";

$data = json_decode(file_get_contents("php://input"), true);

$id_user          = intval($data["id_user"] ?? 0);
$contrasenaActual = trim($data["contrasenaActual"] ?? "");
$nuevaContrasena  = trim($data["nuevaContrasena"] ?? "");

if (!$id_user || !$contrasenaActual || !$nuevaContrasena) {
    echo json_encode(["success" => false, "message" => "Faltan campos requeridos"]);
    exit;
}

// 1. Obtener la contraseña actual encriptada de la base de datos
$stmt = $pdo->prepare("SELECT password FROM usuarios WHERE id_user = ?");
$stmt->execute([$id_user]);
$row = $stmt->fetch(PDO::FETCH_ASSOC);

if (!$row) {
    echo json_encode(["success" => false, "message" => "Usuario no encontrado"]);
    exit;
}

// 2. Verificar si la contraseña actual introducida coincide con el hash almacenado
if (!password_verify($contrasenaActual, $row["password"])) {
    echo json_encode(["success" => false, "message" => "La contraseña actual es incorrecta"]);
    exit;
}

// 3. Generar el hash de la nueva contraseña usando BCRYPT (igual que en register.php)
$newHash = password_hash($nuevaContrasena, PASSWORD_BCRYPT);

// 4. Actualizar en la base de datos
$stmt = $pdo->prepare("UPDATE usuarios SET password = ? WHERE id_user = ?");
$ok = $stmt->execute([$newHash, $id_user]);

echo json_encode(["success" => $ok, "message" => $ok ? "Contraseña actualizada correctamente" : "Error al actualizar"]);
