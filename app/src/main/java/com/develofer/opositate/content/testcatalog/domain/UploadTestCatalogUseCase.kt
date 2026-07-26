package com.develofer.opositate.content.testcatalog.domain

import com.develofer.opositate.R
import com.develofer.opositate.content.testcatalog.data.TestCatalogAssetDataSource
import com.develofer.opositate.main.data.model.Result
import com.develofer.opositate.main.data.provider.ResourceProvider
import javax.inject.Inject

class UploadTestCatalogUseCase @Inject constructor(
    private val testCatalogAssetDataSource: TestCatalogAssetDataSource,
    private val resourceProvider: ResourceProvider,
) {
    suspend operator fun invoke(abilityId: Int = 0): Result<Unit> {
        return try {
            val collection = resourceProvider.getString(R.string.firebase_constant__tests)
            testCatalogAssetDataSource.uploadAbilityCatalog(abilityId, collection)
            Result.Success(Unit)
        } catch (error: Exception) {
            Result.Error(error)
        }
    }
}
