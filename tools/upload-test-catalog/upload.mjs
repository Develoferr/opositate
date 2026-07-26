#!/usr/bin/env node
/**
 * Uploads content/test-catalog/ability-*.json to Firestore collection "tests".
 *
 * Usage:
 *   export GOOGLE_APPLICATION_CREDENTIALS=/path/to/serviceAccount.json
 *   npm install
 *   npm run upload -- --ability 0
 *   npm run upload -- --all
 */
import { readFileSync, readdirSync } from "node:fs";
import { dirname, join, resolve } from "node:path";
import { fileURLToPath } from "node:url";
import { initializeApp, cert, applicationDefault } from "firebase-admin/app";
import { getFirestore } from "firebase-admin/firestore";

const __dirname = dirname(fileURLToPath(import.meta.url));
const repoRoot = resolve(__dirname, "../..");
const catalogDir = join(repoRoot, "content/test-catalog");
const collectionName = "tests";

function parseArgs(argv) {
  const args = { ability: null, all: false };
  for (let i = 0; i < argv.length; i++) {
    const arg = argv[i];
    if (arg === "--all") args.all = true;
    if (arg === "--ability") args.ability = argv[++i];
  }
  return args;
}

function loadCredential() {
  const explicit = process.env.GOOGLE_APPLICATION_CREDENTIALS;
  const localDefault = join(__dirname, "serviceAccount.json");
  try {
    if (explicit) {
      return cert(JSON.parse(readFileSync(explicit, "utf8")));
    }
    return cert(JSON.parse(readFileSync(localDefault, "utf8")));
  } catch {
    try {
      return applicationDefault();
    } catch {
      console.error(
        "Missing credentials. Set GOOGLE_APPLICATION_CREDENTIALS or place serviceAccount.json in this folder.",
      );
      process.exit(1);
    }
  }
}

function catalogFiles(args) {
  if (args.all) {
    return readdirSync(catalogDir)
      .filter((name) => /^ability-\d+\.json$/.test(name))
      .map((name) => join(catalogDir, name))
      .sort();
  }
  const ability = args.ability ?? "0";
  return [join(catalogDir, `ability-${ability}.json`)];
}

async function uploadFile(db, filePath) {
  const raw = readFileSync(filePath, "utf8");
  const data = JSON.parse(raw);
  const documentId = String(data.abilityId ?? filePath.match(/ability-(\d+)/)?.[1]);
  await db.collection(collectionName).doc(documentId).set(data);
  console.log(`Uploaded ${filePath} -> ${collectionName}/${documentId}`);
}

async function main() {
  const args = parseArgs(process.argv.slice(2));
  initializeApp({ credential: loadCredential() });
  const db = getFirestore();
  const files = catalogFiles(args);
  for (const file of files) {
    await uploadFile(db, file);
  }
  console.log("Done.");
}

main().catch((error) => {
  console.error(error);
  process.exit(1);
});
