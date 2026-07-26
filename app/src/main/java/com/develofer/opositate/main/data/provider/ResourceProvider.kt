package com.develofer.opositate.main.data.provider

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ResourceProvider @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    fun getString(resId: Int): String = context.getString(resId)
}