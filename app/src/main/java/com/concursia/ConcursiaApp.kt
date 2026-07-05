package com.concursia

import android.app.Application
import com.concursia.billing.SubscriptionManager
import com.concursia.data.database.ConcursiaDatabase
import com.concursia.data.repository.ConcursiaRepository
import com.concursia.network.ConcursiaApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ConcursiaApp : Application() {

    val database by lazy { ConcursiaDatabase.getDatabase(this) }
    val repository by lazy { ConcursiaRepository(database) }
    val subscriptionManager by lazy { SubscriptionManager(this) }
    val apiClient by lazy { ConcursiaApiClient() }

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        // Carrega dados iniciais em background
        applicationScope.launch {
            repository.loadSampleData()
        }
    }
}