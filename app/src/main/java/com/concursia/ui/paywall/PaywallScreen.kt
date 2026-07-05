package com.concursia.ui.paywall

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.concursia.billing.SubscriptionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallScreen(
    subscriptionManager: SubscriptionManager,
    onPurchaseSuccess: () -> Unit
) {
    val context = LocalContext.current
    val state by subscriptionManager.subscriptionState.collectAsState()
    val scrollState = rememberScrollState()
    var isPurchasing by remember { mutableStateOf(false) }

    // Monitora se a compra foi concluída
    LaunchedEffect(state.isPurchased) {
        if (state.isPurchased && !state.isExpired) {
            isPurchasing = false
            onPurchaseSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Logo
            Text(text = "📚", fontSize = 64.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Concursia",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Card de Preço
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🔥 OFERTA DE LANÇAMENTO 🔥",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "R\$ 1,00",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Text(
                        text = "Acesso por 6 MESES completos!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Menos de R\$ 0,17 por mês! 🚀",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Benefícios
            Text(
                text = "O que você leva?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            val benefits = listOf(
                "📖" to "Conteúdo completo por matéria",
                "📝" to "Simulados estilo CESPE, FCC, FGV",
                "📊" to "Gráficos de progresso e desempenho",
                "📅" to "Cronograma de estudos personalizado",
                "🎯" to "Questões comentadas passo a passo",
                "🔥" to "Estatísticas de acertos por banca"
            )

            benefits.forEach { (icon, text) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = icon, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Lista de concursos inclusos
            Text(
                text = "Concursos inclusos:",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            listOf(
                "INSS", "Banco do Brasil", "Polícia Federal",
                "TCU", "Correios", "SEFAZ-SP", "SABESP"
            ).forEach { name ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = name, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Botão de compra
            Button(
                onClick = {
                    isPurchasing = true
                    subscriptionManager.startPurchase(
                        activity = context as android.app.Activity,
                        onResult = { success, error ->
                            isPurchasing = false
                            if (!success && error != null) {
                                // erro - já está no state
                            }
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                enabled = !isPurchasing
            ) {
                if (isPurchasing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onSecondary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "💰 QUERO ACESSAR POR R\$ 1,00",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Pagamento único • Acesso por 6 meses • Cancele quando quiser",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Pagamento processado pelo Google Play. Renovação não automática - após 6 meses você pode renovar se quiser.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )

            // Mensagem de erro se houver
            state.error?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}