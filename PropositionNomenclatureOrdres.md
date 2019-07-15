# Proposition de nomenclature pour les ordres

`[système] [arguments...]`

## On/Off:
`[système] on/off left/right?` (left/right optionel si c'est un système qui n'a pas de côté)

Exemples:
* `pump left on`
* `pump left off`
* `valve right on`
* `valve right off`

| Argument      | Type                          | Description                       |
|---------------|-------------------------------|-----------------------------------|
| on/off        | 'on' ou 'off'                 | On allume ou on éteint?           |
| left/right    | 'left' ou 'right'             | Côté du robot (Optionnel)         |

## Servo moteur
`servo [id] [angle] [left/right]?` avec `id` l'identifiant du moteur (string?), et `angle` l'angle en degrés

| Argument      | Type                          | Description                       |
|---------------|-------------------------------|-----------------------------------|
| id            | Texte                         | Identifiant du moteur             |
| angle         | Nombre à virgule              | Angle (°) du servomoteur          |
| left/right    | 'left' ou 'right'             | Côté du robot (Optionnel)         |

Exemples:
* `servo elbow 180 left`
* `servo oust 90`

## Groupe de servo moteurs
`servogroup [id] [angles...] [left/right]?`

| Argument      | Type                          | Description                           |
|---------------|-------------------------------|---------------------------------------|
| id            | Texte                         | Identifiant du groupe                 |
| angles        | Liste de nombres à virgule    | Angles (°) de chacun des servomoteurs |
| left/right    | 'left' ou 'right'             | Côté du robot (Optionnel)         |

