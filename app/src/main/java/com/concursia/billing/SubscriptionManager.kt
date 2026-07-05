package com.concursia.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

/**
 * Gerenciador de assinatura do Concursia.
 * Gerencia a compra Play Store + expiração local de 6 meses.
 */
class SubscriptionManager(private val context: Context) {

    companion object {
        private const val SUBSCRIPTION_ID = "concursia_6meses"
        private const val PREFS_NAME = "concursia_subscription"
        private const val KEY_PURCHASE_TOKEN = "purchase_token"
        private const val KEY_EXPIRY_DATE = "expiry_date"
        private const val KEY_PURCHASED = "is_purchased"
        private const val SIX_MONTHS_MS = 180L * 24 * 60 * 60 * 1000
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private var billingClient: BillingClient? = null

    private val _subscriptionState = MutableStateFlow(SubscriptionState())
    val subscriptionState: StateFlow<SubscriptionState> = _subscriptionState.asStateFlow()

    data class SubscriptionState(
        val isPurchased: Boolean = false,
        val expiryDate: Long? = null,
        val daysRemaining: Int = 0,
        val isLoading: Boolean = false,
        val isExpired: Boolean = false,
        val error: String? = null
    )

    /**
     * Inicializa o Billing Client e verifica assinatura existente
     */
    fun initialize(onReady: (Boolean) -> Unit = {}) {
        // Primeiro verifica assinatura local
        checkLocalSubscription()

        billingClient = BillingClient.newBuilder(context)
            .setListener { billingResult, purchases ->
                handlePurchasesUpdated(billingResult, purchases)
            }
            .enablePendingPurchases()
            .build()

        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryExistingPurchases()
                    onReady(true)
                } else {
                    _subscriptionState.value = _subscriptionState.value.copy(
                        error = "Erro ao conectar na Play Store: ${billingResult.debugMessage}"
                    )
                    onReady(false)
                }
            }

            override fun onBillingServiceDisconnected() {
                // Tentar reconectar
                billingClient?.startConnection(this)
            }
        })
    }

    /**
     * Verifica assinatura salva localmente
     */
    private fun checkLocalSubscription() {
        val isPurchased = prefs.getBoolean(KEY_PURCHASED, false)
        val expiryDate = prefs.getLong(KEY_EXPIRY_DATE, 0L)
        val now = System.currentTimeMillis()

        if (isPurchased && expiryDate > now) {
            val daysRemaining = ((expiryDate - now) / (24 * 60 * 60 * 1000)).toInt()
            _subscriptionState.value = SubscriptionState(
                isPurchased = true,
                expiryDate = expiryDate,
                daysRemaining = daysRemaining,
                isExpired = false
            )
        } else if (isPurchased && expiryDate <= now) {
            // Assinatura expirou
            prefs.edit()
                .putBoolean(KEY_PURCHASED, false)
                .remove(KEY_EXPIRY_DATE)
                .remove(KEY_PURCHASE_TOKEN)
                .apply()
            _subscriptionState.value = SubscriptionState(isExpired = true)
        }
    }

    /**
     * Consulta compras existentes no Google Play
     */
    private fun queryExistingPurchases() {
        billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(ProductType.SUBS)
                .build()
        ) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases.isNotEmpty()) {
                for (purchase in purchases) {
                    handlePurchase(purchase)
                }
            }
        }
    }

    /**
     * Inicia o fluxo de compra da assinatura de 6 meses
     */
    fun startPurchase(activity: Activity, onResult: (Boolean, String?) -> Unit = { _, _ -> }) {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(SUBSCRIPTION_ID)
                .setProductType(ProductType.SUBS)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient?.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (productDetailsList.isNotEmpty()) {
                    val productDetails = productDetailsList.first()
                    val offerToken = productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken ?: ""

                    val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .setOfferToken(offerToken)
                        .build()

                    val billingFlowParams = BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(listOf(productDetailsParams))
                        .build()

                    val response = billingClient?.launchBillingFlow(activity, billingFlowParams)
                    if (response?.responseCode != BillingClient.BillingResponseCode.OK) {
                        onResult(false, "Não foi possível iniciar a compra")
                    }
                } else {
                    onResult(false, "Assinatura não encontrada na Play Store")
                }
            } else {
                onResult(false, "Erro ao consultar produtos: ${billingResult.debugMessage}")
            }
        }
    }

    /**
     * Processa compra confirmada
     */
    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            val expiryDate = System.currentTimeMillis() + SIX_MONTHS_MS
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))

            prefs.edit()
                .putBoolean(KEY_PURCHASED, true)
                .putLong(KEY_EXPIRY_DATE, expiryDate)
                .putString(KEY_PURCHASE_TOKEN, purchase.purchaseToken)
                .apply()

            _subscriptionState.value = SubscriptionState(
                isPurchased = true,
                expiryDate = expiryDate,
                daysRemaining = SIX_MONTHS_MS / (24 * 60 * 60 * 1000).toInt(),
                isExpired = false,
                isLoading = false
            )

            // Confirma consumo (acknowledge) - obrigatório para subs
            val acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            billingClient?.acknowledgePurchase(acknowledgeParams) { _ -> }
        }
    }

    private fun handlePurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.forEach { handlePurchase(it) }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                _subscriptionState.value = _subscriptionState.value.copy(
                    isLoading = false,
                    error = "Compra cancelada"
                )
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                queryExistingPurchases()
            }
            else -> {
                _subscriptionState.value = _subscriptionState.value.copy(
                    isLoading = false,
                    error = "Erro na compra: ${billingResult.debugMessage}"
                )
            }
        }
    }

    /**
     * Verifica se o usuário tem acesso ativo
     */
    fun hasActiveSubscription(): Boolean {
        val state = _subscriptionState.value
        return state.isPurchased && !state.isExpired
    }

    /**
     * Libera/Desbloqueia manualmente (para testes ou autorização por email)
     */
    fun grantAccess(durationMs: Long = SIX_MONTHS_MS) {
        val expiryDate = System.currentTimeMillis() + durationMs
        prefs.edit()
            .putBoolean(KEY_PURCHASED, true)
            .putLong(KEY_EXPIRY_DATE, expiryDate)
            .apply()

        _subscriptionState.value = SubscriptionState(
            isPurchased = true,
            expiryDate = expiryDate,
            daysRemaining = (durationMs / (24 * 60 * 60 * 1000)).toInt(),
            isExpired = false
        )
    }

    /**
     * Retorna data de expiração formatada
     */
    fun getExpiryDateFormatted(): String {
        val expiry = prefs.getLong(KEY_EXPIRY_DATE, 0L)
        if (expiry == 0L) return "---"
        val sdf = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("pt", "BR"))
        return sdf.format(Date(expiry))
    }

    fun destroy() {
        billingClient?.endConnection()
    }
}