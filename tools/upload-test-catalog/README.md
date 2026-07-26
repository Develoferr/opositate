# Upload test catalog (Firebase Admin)

Sube el banco de preguntas desde `content/test-catalog/*.json` a Firestore (`tests/{abilityId}`).

Misma fuente JSON que usa la app en debug (`UploadTestCatalogUseCase` + assets).

## Requisitos

1. Node.js 20+
2. Service account de Firebase con permiso de escritura en Firestore
3. Coloca la clave como `tools/upload-test-catalog/serviceAccount.json` **o** exporta:

```bash
export GOOGLE_APPLICATION_CREDENTIALS=/ruta/a/tu-serviceAccount.json
```

`serviceAccount.json` está en `.gitignore`. No lo subas al repo.

## Uso

```bash
cd tools/upload-test-catalog
npm install
npm run upload -- --ability 0
npm run upload -- --all
```

## Añadir más abilities

1. Crea `content/test-catalog/ability-N.json` con la misma forma:

```json
{
  "abilityId": 0,
  "tasks": [
    {
      "taskId": 0,
      "difficultyLevels": [
        {
          "difficultyId": 0,
          "tests": [
            {
              "testId": 0,
              "questions": [
                {
                  "id": 0,
                  "question": "...",
                  "options": ["A", "B", "C", "D"],
                  "correctAnswer": 0
                }
              ]
            }
          ]
        }
      ]
    }
  ]
}
```

2. Sube con el script o, en un build **debug** de la app, menú ⋮ → «Subir catálogo de tests» (solo ability 0 por defecto en la app).
