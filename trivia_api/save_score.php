<?php
// =============================================================
//  save_score.php  –  Guardar puntuación
// =============================================================
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");

require_once "db.php";

$data = json_decode(file_get_contents("php://input"), true);

$id_user    = intval($data["id_user"]    ?? 0);
$categoria  = trim($data["categoria"]   ?? "");
$puntuacion = intval($data["puntuacion"] ?? 0);

if (!$id_user || !$categoria) {
    echo json_encode(["success" => false, "message" => "Faltan campos"]);
    exit;
}

$fecha = date("Y-m-d H:i");
$stmt = $pdo->prepare(
    "INSERT INTO puntuaciones (id_user, categoria, puntuacion, fecha) VALUES (?, ?, ?, ?)"
);
$ok = $stmt->execute([$id_user, $categoria, $puntuacion, $fecha]);

echo json_encode(["success" => $ok]);
