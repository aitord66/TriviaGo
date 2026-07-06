# Trivia Go!

Aplicación Android de trivia desarrollada como Trabajo de Fin de Ciclo (TFG) de 2ºDAM.

## Descripción

Trivia Go! permite a los usuarios registrarse, competir en 7 categorías de preguntas de cultura general y ver sus puntuaciones en un ranking diario que se reinicia automáticamente cada medianoche.

## Funcionalidades

- Registro e inicio de sesión con autenticación segura (bcrypt)
- 7 categorías: Historia, Ciencia, Geografía, Deportes, Videojuegos, Películas y Música
- Sistema de puntuación por velocidad de respuesta (hasta 1000 pts por pregunta)
- Imágenes asociadas a las preguntas
- Ranking diario con filtro por categoría y contador de reinicio
- Tema oscuro y claro intercambiables
- Sesión persistente entre cierres de la app

## Tecnologías

| Capa | Tecnología |
|------|------------|
| App Android | Kotlin |
| Backend | PHP 8 |
| Base de datos | MariaDB |
| Servidor | Apache (Linux) |
| Diseño | Material Design |

## Estructura del proyecto
TriviaGo/
├── app/                  # Aplicación Android (Kotlin)
│   └── src/main/
│       ├── java/         # Código fuente
│       └── assets/       # Preguntas en JSON
└── trivia_api/           # Backend PHP
├── db.php
├── register.php
├── login.php
├── save_score.php
├── get_ranking.php
└── get_best_score.php

## Requisitos

- Android 8.0 (API 26) o superior
- Servidor con Apache + PHP 8 + MariaDB
