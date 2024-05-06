package com.example.bluromatic.data

import androidx.work.WorkInfo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BluromaticRepositoryImpl @Inject constructor(): BluromaticRepository {
    override val outputWorkInfo: Flow<WorkInfo?>
        get() = TODO("Not yet implemented")

    override fun applyBlur(blurLevel: Int) {
        TODO("Not yet implemented")
    }

    override fun cancelWork() {
        TODO("Not yet implemented")
    }
}