package com.develofer.opositate.feature.profile.data.repository

import com.develofer.opositate.R
import com.develofer.opositate.feature.profile.data.model.UserScoresResponse
import com.develofer.opositate.feature.profile.domain.repository.UserRepository
import com.develofer.opositate.feature.test.data.model.CompleteTestAsksResult
import com.develofer.opositate.feature.test.data.model.toDomain
import com.develofer.opositate.feature.test.domain.model.CompleteTestAsksList
import com.develofer.opositate.main.data.model.Result
import com.develofer.opositate.main.data.provider.ResourceProvider
import com.develofer.opositate.utils.StringConstants.EMPTY_STRING
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val resourceProvider: ResourceProvider
) : UserRepository {

    override suspend fun getUserName(): Result<String> {
        val user = getUser()

        return if (user?.displayName.isNullOrEmpty()) {
            Result.Error(Exception(resourceProvider.getString(R.string.error_message__no_display_name)))
        } else {
            Result.Success(user.displayName ?: EMPTY_STRING)
        }
    }

    override suspend fun getUserEmail(): Result<String> {
        val user = getUser()
        return if (user?.email.isNullOrEmpty()) {
            Result.Error(Exception(resourceProvider.getString(R.string.error_message__no_email)))
        } else {
            Result.Success(user.email ?: EMPTY_STRING)
        }
    }

    override suspend fun getUserId(): Result<String> {
        val user = getUser()
        return if (user?.uid.isNullOrBlank()) {
            Result.Error(Exception(resourceProvider.getString(R.string.error_message__user_not_authenticated)))
        } else {
            Result.Success(user.uid)
        }
    }

    override suspend fun createUserScoreDocument(abilityIdList: List<Map<String, Any>>): Result<Unit> {
        val userId = getUser()?.uid
        return if (userId.isNullOrBlank()) {
            Result.Error(Exception(resourceProvider.getString(R.string.error_message__user_not_authenticated)))
        } else {
            val userScoreDocument = getUserScoreDocumentReference(userId)
                ?: return Result.Error(Exception(resourceProvider.getString(R.string.error_message__create_score_document_failed)))
            val userScores: List<Map<String, Any>> = getUserScoresMap(abilityIdList)
            suspendCancellableCoroutine { continuation ->
                userScoreDocument.set(
                    mapOf(
                        resourceProvider.getString(R.string.firebase_constant__level) to 0,
                        resourceProvider.getString(R.string.firebase_constant__scores) to userScores
                    )
                ).addOnCompleteListener { createTask ->
                    if (createTask.isSuccessful) {
                        continuation.resume(Result.Success(Unit))
                    } else {
                        val errorMessage = createTask.exception?.message
                            ?: resourceProvider.getString(R.string.error_message__create_score_document_failed)
                        continuation.resume(Result.Error(Exception(errorMessage)))
                    }
                }
            }
        }
    }

    override suspend fun createTestAsksDocument(abilityIdList: List<Map<String, Any>>): Result<Unit> {
        val userId = getUser()?.uid
        return if (userId.isNullOrBlank()) {
            Result.Error(Exception(resourceProvider.getString(R.string.error_message__user_not_authenticated)))
        } else {
            val testAsksDocument = getTestAsksDocumentReference(userId)
                ?: return Result.Error(Exception(resourceProvider.getString(R.string.error_message__create_score_document_failed)))
            val testAsks: List<Map<String, Any>> = getTestAsksMap(abilityIdList)
            suspendCancellableCoroutine { continuation ->
                testAsksDocument.set(
                    mapOf(
                        resourceProvider.getString(R.string.firebase_constant__test_asks) to testAsks
                    )
                ).addOnCompleteListener { createTask ->
                    if (createTask.isSuccessful) {
                        continuation.resume(Result.Success(Unit))
                    } else {
                        val errorMessage = createTask.exception?.message
                            ?: resourceProvider.getString(R.string.error_message__create_score_document_failed)
                        continuation.resume(Result.Error(Exception(errorMessage)))
                    }
                }
            }
        }
    }

    override suspend fun getUserScoreResponse(): Result<UserScoresResponse?> {
        val userId = getUser()?.uid
        return if (userId != null) {
            suspendCancellableCoroutine { continuation ->
                val documentReference = getUserScoreDocumentReference(userId)
                documentReference?.get()
                    ?.addOnSuccessListener { document ->
                        if (document.exists()) {
                            val userScoresResponse =
                                document.toObject(UserScoresResponse::class.java)
                            continuation.resume(Result.Success(userScoresResponse))
                        } else {
                            continuation.resume(Result.Error(Exception(resourceProvider.getString(R.string.error_message__no_scores_document))))
                        }
                    }
                    ?.addOnFailureListener { exception ->
                        continuation.resume(Result.Error(Exception(exception.message)))
                    }
            }
        } else {
            Result.Error(Exception(resourceProvider.getString(R.string.error_message__user_not_authenticated)))
        }
    }

    override suspend fun getTestAsksResponse(): Result<CompleteTestAsksList?> {
        val userId = getUser()?.uid
        return if (userId != null) {
            suspendCancellableCoroutine { continuation ->
                val documentReference = getTestAsksDocumentReference(userId)
                documentReference?.get()
                    ?.addOnSuccessListener { document ->
                        if (document.exists()) {
                            val testAsksResponse =
                                document.toObject(CompleteTestAsksResult::class.java)?.toDomain()
                            continuation.resume(Result.Success(testAsksResponse))
                        } else {
                            continuation.resume(Result.Error(Exception(resourceProvider.getString(R.string.error_message__no_asks_document))))
                        }
                    }
                    ?.addOnFailureListener { exception ->
                        continuation.resume(Result.Error(Exception(exception.message)))
                    }
            }
        } else {
            Result.Error(Exception(resourceProvider.getString(R.string.error_message__user_not_authenticated)))
        }
    }

    private fun getUser() = auth.currentUser

    private fun getUserScoresMap(abilityIdList: List<Map<String, Any>>): List<Map<String, Any>> {
        val userScores: MutableList<Map<String, Any>> = mutableListOf()
        val abilityIdString = resourceProvider.getString(R.string.firebase_constant__ability_id)
        val tasksString = resourceProvider.getString(R.string.firebase_constant__tasks)

        abilityIdList.forEach { abilityId ->
            val abilityIdValue = abilityId[abilityIdString] as? Int ?: return@forEach
            val tasks = abilityId[tasksString] as? List<*> ?: return@forEach
            val tasksWithScores = tasks.map { task ->
                mapOf(
                    resourceProvider.getString(R.string.firebase_constant__task_id) to (task),
                    resourceProvider.getString(R.string.firebase_constant__start_score) to 0,
                    resourceProvider.getString(R.string.firebase_constant__present_score) to 0)
            }
            userScores.add(
                mapOf(
                    abilityIdString to abilityIdValue,
                    resourceProvider.getString(R.string.firebase_constant__start_score) to 0,
                    resourceProvider.getString(R.string.firebase_constant__present_score) to 0,
                    resourceProvider.getString(R.string.firebase_constant__tasks_scores) to tasksWithScores
                )
            )
        }
        return userScores.toList()
    }

    private fun getTestAsksMap(abilityIdList: List<Map<String, Any>>): List<Map<String, Any>> {
        val testAsks: MutableList<Map<String, Any>> = mutableListOf()
        val abilityIdString = resourceProvider.getString(R.string.firebase_constant__ability_id)
        val tasksString = resourceProvider.getString(R.string.firebase_constant__tasks)

        abilityIdList.forEach { abilityId ->
            val abilityIdValue = abilityId[abilityIdString] as? Int ?: return@forEach
            val tasks = abilityId[tasksString] as? List<*> ?: return@forEach
            val tasksAsks = tasks.map { task ->
                mapOf(
                    resourceProvider.getString(R.string.firebase_constant__task_id) to (task),
                    resourceProvider.getString(R.string.firebase_constant__approved_tests) to 0,
                    resourceProvider.getString(R.string.firebase_constant__failed_test) to 0)
            }
            testAsks.add(
                mapOf(
                    abilityIdString to abilityIdValue,
                    resourceProvider.getString(R.string.firebase_constant__approved_tests) to 0,
                    resourceProvider.getString(R.string.firebase_constant__failed_test) to 0,
                    resourceProvider.getString(R.string.firebase_constant__task_asks) to tasksAsks
                )
            )
        }
        return testAsks.toList()
    }

    private fun getUserScoreDocumentReference(userId: String?): DocumentReference? {
        val scoresCollection =
            firestore.collection(resourceProvider.getString(R.string.firebase_constant__scores))
        val userScoreDocument = userId?.let { scoresCollection.document(it) }
        return userScoreDocument
    }

    private fun getTestAsksDocumentReference(userId: String?): DocumentReference? {
        val testAsksCollection =
            firestore.collection(resourceProvider.getString(R.string.firebase_constant__test_asks))
        val testAsksDocument = userId?.let { testAsksCollection.document(it) }
        return testAsksDocument
    }
}