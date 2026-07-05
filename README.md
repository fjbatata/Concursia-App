# Concursia 📚

**Seu professor de concursos públicos — inteligente, interativo e acessível.**

## 🧠 Sobre o App

O **Concursia** é um aplicativo Android que transforma seu celular em um professor particular de concursos públicos. Com conteúdo didático, questões comentadas e simulados, ele te guia matéria por matéria até a aprovação.

## ✨ Funcionalidades

- 📖 **Conteúdo Interativo** — Teoria completa com mnemônicos, tabelas e dicas de banca
- 📝 **Simulados Inteligentes** — Questões estilo CESPE, FCC e FGV com explicações detalhadas
- 📊 **Progresso Detalhado** — Gráficos de desempenho, tempo de estudo e evolução
- 🎯 **Foco em Resultados** — Estatísticas de acertos por matéria e banca
- 🔥 **Cronograma Personalizado** — Plano de estudos baseado no seu concurso alvo
- 💰 **Preço Único** — R$ 1,00 por 6 meses de acesso completo

## 🗺️ Estrutura do App

```
Concursia/
├── app/
│   └── src/main/java/com/concursia/
│       ├── ConcursiaApp.kt          # Application class
│       ├── MainActivity.kt          # Entry point
│       ├── billing/
│       │   └── SubscriptionManager.kt  # Google Play Billing (R$1/6meses)
│       ├── data/
│       │   ├── database/
│       │   │   ├── ConcursiaDatabase.kt
│       │   │   ├── dao/Daos.kt
│       │   │   └── entity/Entities.kt
│       │   └── repository/
│       │       └── ConcursiaRepository.kt  # + Sample Data real
│       ├── navigation/
│       │   ├── Screen.kt
│       │   └── NavGraph.kt
│       └── ui/
│           ├── splash/SplashScreen.kt
│           ├── paywall/PaywallScreen.kt
│           ├── home/HomeScreen.kt
│           ├── concurso/ConcursoDetailScreen.kt
│           ├── subject/SubjectDetailScreen.kt
│           ├── study/StudyScreen.kt
│           ├── quiz/QuizScreen.kt
│           └── progress/ProgressScreen.kt
```

## 🚀 Como Publicar na Play Store

### 1. Conta de Desenvolvedor
- Crie uma conta em [play.google.com/console](https://play.google.com/console)
- Pague a taxa única de US$ 25

### 2. Configurar Produto de Assinatura
No Play Console:
1. Vá em **Produtos** > **Assinaturas**
2. Crie uma assinatura com ID: `concursia_6meses`
3. Preço: **R$ 1,00**
4. Período de faturamento: **6 meses** (ou crie como pagamento único com duração de 6 meses)
5. Marque como "Produto gerenciado" (não renovável automaticamente)

### 3. Gerar APK Assinado
```bash
# No Android Studio:
Build > Generate Signed Bundle / APK
# Escolha APK ou App Bundle (recomendado: App Bundle)
# Siga o wizard, gere a keystore e assine
```

### 4. Enviar para Play Store
- Crie um novo app no Play Console
- Preencha as informações (descrição, screenshots, etc.)
- Faça upload do AAB ou APK
- Defina preço como **R$ 1,00**
- Publique!

## 🛠️ Tecnologias

| Tecnologia | Versão | Uso |
|-----------|--------|-----|
| Kotlin | 1.9.20 | Linguagem principal |
| Jetpack Compose | BOM 2024.01 | UI declarativa |
| Room | 2.6.1 | Banco de dados local |
| Navigation Compose | 2.7.6 | Navegação entre telas |
| Google Play Billing | 6.x | Assinatura de 6 meses |
| Material 3 | Atual | Design system |
| Coroutines | 1.7.3 | Async |
| KSP | 1.9.20 | Processamento Room |

## 📱 Telas do App

1. **Splash** → Animação de abertura
2. **Paywall** → Tela de compra (R$ 1,00)
3. **Home** → Lista de concursos + ações rápidas
4. **Detalhes do Concurso** → Info + matérias
5. **Matéria** → Tópicos com progresso
6. **Estudo** → Conteúdo interativo completo
7. **Simulado** → Questões com correção
8. **Progresso** → Estatísticas do usuário

## 📄 Licença

Proprietário — BrainLogic AI