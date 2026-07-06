<?php
// =============================================================
//  get_ranking.php  –  Mejor marca por usuario con filtro y contador
//  Solo muestra puntuaciones del día actual (reinicio a medianoche)
// =============================================================
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");

require_once "db.php";

$limit     = intval($_GET["limit"] ?? 20);
$categoria = $_GET["categoria"] ?? null;

// 1. Construir la consulta SQL filtrando por fecha de hoy
$sql = "
SELECT u.username,
p.categoria,
MAX(p.puntuacion) AS puntuacion,
MAX(p.fecha)      AS fecha
FROM puntuaciones p
JOIN usuarios u ON p.id_user = u.id_user
WHERE DATE(p.fecha) = CURDATE()
";

if ($categoria) {
    $sql .= " AND p.categoria = ? ";
}

$sql .= "
GROUP BY u.username, p.categoria
ORDER BY puntuacion DESC
LIMIT " . intval($limit);

$stmt = $pdo->prepare($sql);
if ($categoria) {
    $stmt->execute([$categoria]);
} else {
    $stmt->execute();
}

$rows = $stmt->fetchAll(PDO::FETCH_ASSOC);

// 2. Calcular los segundos restantes hasta medianoche
$ahora = new DateTime();
$medianoche = new DateTime('tomorrow midnight');
$segundos_restantes = $medianoche->getTimestamp() - $ahora->getTimestamp();

echo json_encode([
    "success" => true,
    "ranking" => $rows,
    "tiempo_restante_segundos" => $segundos_restantes
]);
