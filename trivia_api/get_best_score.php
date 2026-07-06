<?php
// =============================================================
//  get_best_score.php  –  Mejor puntuación de un usuario en una categoría
//  POST: { id_user, categoria }
// =============================================================
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");

require_once "db.php";

$data = json_decode(file_get_contents("php://input"), true);

$id_user   = intval($data["id_user"]   ?? 0);
$categoria = trim($data["categoria"]  ?? "");

$stmt = $pdo->prepare(
    "SELECT MAX(puntuacion) AS mejor FROM puntuaciones WHERE id_user = ? AND categoria = ?"
);
$stmt->execute([$id_user, $categoria]);
$row = $stmt->fetch(PDO::FETCH_ASSOC);

echo json_encode(["success" => true, "mejor" => intval($row["mejor"] ?? 0)]);
