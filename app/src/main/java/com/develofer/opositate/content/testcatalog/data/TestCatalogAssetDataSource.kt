package com.develofer.opositate.content.testcatalog.data

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.longOrNull
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class TestCatalogAssetDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val firestore: FirebaseFirestore,
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun uploadAbilityCatalog(abilityId: Int, collectionName: String) {
        val assetPath = "test-catalog/ability-$abilityId.json"
        val raw = context.assets.open(assetPath).bufferedReader().use { it.readText() }
        val root = json.parseToJsonElement(raw).asMap()
        val documentId = root["abilityId"]?.toString() ?: abilityId.toString()
        suspendCancellableCoroutine { continuation ->
            firestore.collection(collectionName).document(documentId)
                .set(root)
                .addOnSuccessListener { continuation.resume(Unit) }
                .addOnFailureListener { error -> continuation.resumeWithException(error) }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun JsonElement.asMap(): Map<String, Any> {
        require(this is JsonObject) { "Catalog root must be a JSON object" }
        return toFirestoreValue() as Map<String, Any>
    }

    private fun JsonElement.toFirestoreValue(): Any? = when (this) {
        is JsonNull -> null
        is JsonPrimitive -> when {
            isString -> content
            booleanOrNull != null -> booleanOrNull
            longOrNull != null -> {
                val longValue = longOrNull!!
                if (longValue in Int.MIN_VALUE..Int.MAX_VALUE) longValue.toInt() else longValue
            }
            doubleOrNull != null -> doubleOrNull
            else -> contentOrNull
        }
        is JsonArray -> map { it.toFirestoreValue() }
        is JsonObject -> entries.associate { (key, value) -> key to value.toFirestoreValue() }
    }
}
