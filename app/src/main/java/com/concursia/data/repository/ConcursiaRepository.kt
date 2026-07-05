package com.concursia.data.repository

import com.concursia.data.database.ConcursiaDatabase
import com.concursia.data.database.entity.*
import kotlinx.coroutines.flow.Flow

class ConcursiaRepository(private val db: ConcursiaDatabase) {

    // === CONCURSOS ===
    fun getAllConcursos() = db.concursoDao().getAllConcursos()
    fun getActiveConcursos() = db.concursoDao().getActiveConcursos()
    fun getFavoriteConcursos() = db.concursoDao().getFavoriteConcursos()
    suspend fun getConcursoById(id: String) = db.concursoDao().getConcursoById(id)
    suspend fun toggleFavorite(id: String, isFavorite: Boolean) =
        db.concursoDao().toggleFavorite(id, isFavorite)

    // === SUBJECTS ===
    fun getSubjectsByConcurso(concursoId: String) = db.subjectDao().getSubjectsByConcurso(concursoId)
    fun getSubjectById(id: String) = db.subjectDao().getSubjectById(id)

    // === TOPICS ===
    fun getTopicsBySubject(subjectId: String) = db.topicDao().getTopicsBySubject(subjectId)
    suspend fun getTopicById(id: String) = db.topicDao().getTopicById(id)
    suspend fun getNextTopics(concursoId: String) = db.topicDao().getNextTopics(concursoId)
    suspend fun markTopicCompleted(id: String) = db.topicDao().markCompleted(id)
    fun getCompletedTopicsCount(subjectId: String) = db.topicDao().getCompletedTopicsCount(subjectId)
    fun getTotalTopicsCount(subjectId: String) = db.topicDao().getTotalTopicsCount(subjectId)

    // === QUESTIONS ===
    suspend fun getQuestionsByTopic(topicId: String) = db.questionDao().getQuestionsByTopic(topicId)
    suspend fun getRandomQuestions(concursoId: String, limit: Int = 10) =
        db.questionDao().getRandomQuestions(concursoId, limit)
    suspend fun getRandomQuestionsBySubjects(concursoId: String, subjectIds: List<String>, limit: Int = 10) =
        db.questionDao().getRandomQuestionsBySubjects(concursoId, subjectIds, limit)

    // === QUIZ ===
    fun getAllQuizAttempts() = db.quizAttemptDao().getAllAttempts()
    fun getQuizAttemptsByConcurso(concursoId: String) = db.quizAttemptDao().getAttemptsByConcurso(concursoId)
    fun getAverageScore(concursoId: String) = db.quizAttemptDao().getAverageScore(concursoId)
    suspend fun saveQuizAttempt(attempt: QuizAttemptEntity) = db.quizAttemptDao().insertAttempt(attempt)

    // === STUDY ===
    fun getTotalStudyTime() = db.studySessionDao().getTotalStudyTime()
    fun getStudyTimeByConcurso(concursoId: String) = db.studySessionDao().getStudyTimeByConcurso(concursoId)
    fun getRecentSessions() = db.studySessionDao().getRecentSessions()
    fun getStudyDays(since: Long) = db.studySessionDao().getStudyDays(since)
    suspend fun saveStudySession(session: StudySessionEntity) = db.studySessionDao().insertSession(session)

    /**
     * Carrega dados iniciais de exemplo com concursos reais brasileiros
     */
    suspend fun loadSampleData() {
        val existing = db.concursoDao().getConcursosCount()
        if (existing > 0) return

        val now = System.currentTimeMillis()

        // -- CONCURSOS --
        val concursos = listOf(
            ConcursoEntity(
                id = "inss_2026",
                title = "INSS 2026",
                description = "Concurso para Técnico do Seguro Social. Vagas em todo o Brasil com salário inicial de R\$ 5.905,00.",
                banca = "CESPE/CEBRASPE",
                level = "Federal",
                status = "Vigente",
                vacancies = 1500,
                registrationStart = "01/03/2026",
                registrationEnd = "31/03/2026",
                examDate = "18/05/2026",
                salary = "R\$ 5.905,00",
                imageUrl = null,
                subjects = listOf("direito-constitucional", "direito-administrativo", "portugues", "raciocinio-logico", "previdenciario", "informatica"),
                isFavorite = true,
                createdAt = now
            ),
            ConcursoEntity(
                id = "policia_federal_2026",
                title = "Polícia Federal 2026",
                description = "Concurso para Agente e Escrivão da Polícia Federal. Exige nível superior.",
                banca = "CESPE/CEBRASPE",
                level = "Federal",
                status = "Vigente",
                vacancies = 800,
                registrationStart = "15/04/2026",
                registrationEnd = "15/05/2026",
                examDate = "13/07/2026",
                salary = "R\$ 7.500,00",
                imageUrl = null,
                subjects = listOf("direito-constitucional", "direito-administrativo", "direito-penal", "direito-processual-penal", "portugues", "informatica"),
                isFavorite = false,
                createdAt = now
            ),
            ConcursoEntity(
                id = "banco_brasil_2026",
                title = "Banco do Brasil 2026",
                description = "Concurso para Escriturário do Banco do Brasil. Vagas para nível médio.",
                banca = "FCC",
                level = "Federal",
                status = "Vigente",
                vacancies = 2000,
                registrationStart = "10/02/2026",
                registrationEnd = "10/03/2026",
                examDate = "27/04/2026",
                salary = "R\$ 5.400,00 + benefícios",
                imageUrl = null,
                subjects = listOf("portugues", "raciocinio-logico", "conhecimentos-bancarios", "informatica", "matematica-financeira", "atendimento"),
                isFavorite = true,
                createdAt = now
            ),
            ConcursoEntity(
                id = "tcu_2026",
                title = "TCU 2026",
                description = "Concurso para Auditor Federal de Controle Externo do Tribunal de Contas da União.",
                banca = "CESPE/CEBRASPE",
                level = "Federal",
                status = "Previsto",
                vacancies = 100,
                registrationStart = "Previsto",
                registrationEnd = "Previsto",
                examDate = "Previsto",
                salary = "R\$ 16.000,00",
                imageUrl = null,
                subjects = listOf("direito-constitucional", "direito-administrativo", "direito-financeiro", "auditoria", "portugues", "raciocinio-logico"),
                isFavorite = false,
                createdAt = now
            ),
            ConcursoEntity(
                id = "correios_2026",
                title = "Correios 2026",
                description = "Concurso dos Correios para carteiro e operador de triagem. Nível médio.",
                banca = "IBFC",
                level = "Federal",
                status = "Previsto",
                vacancies = 3000,
                registrationStart = "Previsto",
                registrationEnd = "Previsto",
                examDate = "Previsto",
                salary = "R\$ 2.400,00",
                imageUrl = null,
                subjects = listOf("portugues", "raciocinio-logico", "informatica", "conhecimentos-gerais"),
                isFavorite = true,
                createdAt = now
            ),
            ConcursoEntity(
                id = "sabesp_2026",
                title = "SABESP 2026",
                description = "Concurso da Companhia de Saneamento Básico do Estado de São Paulo.",
                banca = "FCC",
                level = "Estadual",
                status = "Vigente",
                vacancies = 400,
                registrationStart = "01/05/2026",
                registrationEnd = "01/06/2026",
                examDate = "20/07/2026",
                salary = "R\$ 3.500,00",
                imageUrl = null,
                subjects = listOf("portugues", "raciocinio-logico", "informatica", "conhecimentos-especificos"),
                isFavorite = false,
                createdAt = now
            ),
            ConcursoEntity(
                id = "sefaz_sp_2026",
                title = "SEFAZ-SP 2026",
                description = "Concurso para Auditor Fiscal da Receita Estadual de São Paulo.",
                banca = "FGV",
                level = "Estadual",
                status = "Previsto",
                vacancies = 200,
                registrationStart = "Previsto",
                registrationEnd = "Previsto",
                examDate = "Previsto",
                salary = "R\$ 13.000,00",
                imageUrl = null,
                subjects = listOf("direito-tributario", "direito-constitucional", "direito-administrativo", "contabilidade", "portugues", "raciocinio-logico"),
                isFavorite = false,
                createdAt = now
            ),
            // ================================================================
            // CONCURSOS DA BAIXADA SANTISTA
            // ================================================================
            ConcursoEntity(
                id = "pref_santos_2026",
                title = "Prefeitura de Santos 2026",
                description = "Concurso público da Prefeitura de Santos para diversos cargos municipais. Baixada Santista - SP.",
                banca = "FGV / VUNESP",
                level = "Municipal",
                status = "Vigente",
                vacancies = 500,
                registrationStart = "01/08/2026",
                registrationEnd = "31/08/2026",
                examDate = "12/10/2026",
                salary = "R\$ 2.500 a R\$ 8.000",
                imageUrl = null,
                subjects = listOf("portugues", "raciocinio-logico", "informatica", "conhecimentos-especificos", "direito-constitucional", "direito-administrativo"),
                isFavorite = false,
                createdAt = now
            ),
            ConcursoEntity(
                id = "cm_santos_2026",
                title = "Câmara de Santos 2026",
                description = "Concurso da Câmara Municipal de Santos para cargos de nível médio e superior.",
                banca = "VUNESP",
                level = "Municipal",
                status = "Previsto",
                vacancies = 80,
                registrationStart = "Previsto",
                registrationEnd = "Previsto",
                examDate = "Previsto",
                salary = "R\$ 3.500 a R\$ 9.000",
                imageUrl = null,
                subjects = listOf("portugues", "raciocinio-logico", "informatica", "legislacao-municipal", "direito-constitucional"),
                isFavorite = true,
                createdAt = now
            ),
            ConcursoEntity(
                id = "pref_saovicente_2026",
                title = "Prefeitura de São Vicente 2026",
                description = "Concurso da Prefeitura de São Vicente com vagas para diversos cargos municipais. Baixada Santista - SP.",
                banca = "VUNESP",
                level = "Municipal",
                status = "Vigente",
                vacancies = 300,
                registrationStart = "15/06/2026",
                registrationEnd = "15/07/2026",
                examDate = "24/08/2026",
                salary = "R\$ 2.200 a R\$ 7.000",
                imageUrl = null,
                subjects = listOf("portugues", "raciocinio-logico", "informatica", "conhecimentos-especificos", "direito-administrativo"),
                isFavorite = false,
                createdAt = now
            ),
            ConcursoEntity(
                id = "pref_praiagrande_2026",
                title = "Prefeitura de Praia Grande 2026",
                description = "Concurso da Prefeitura de Praia Grande para cargos administrativos e operacionais.",
                banca = "VUNESP",
                level = "Municipal",
                status = "Previsto",
                vacancies = 250,
                registrationStart = "Previsto",
                registrationEnd = "Previsto",
                examDate = "Previsto",
                salary = "R\$ 2.500 a R\$ 6.000",
                imageUrl = null,
                subjects = listOf("portugues", "raciocinio-logico", "informatica", "conhecimentos-especificos"),
                isFavorite = false,
                createdAt = now
            ),
            ConcursoEntity(
                id = "pref_guaruja_2026",
                title = "Prefeitura de Guarujá 2026",
                description = "Concurso público da Prefeitura de Guarujá. Vagas para ensino médio e superior.",
                banca = "IBFC / VUNESP",
                level = "Municipal",
                status = "Vigente",
                vacancies = 200,
                registrationStart = "20/07/2026",
                registrationEnd = "20/08/2026",
                examDate = "14/09/2026",
                salary = "R\$ 2.300 a R\$ 7.500",
                imageUrl = null,
                subjects = listOf("portugues", "raciocinio-logico", "informatica", "direito-constitucional", "conhecimentos-especificos"),
                isFavorite = false,
                createdAt = now
            ),
            ConcursoEntity(
                id = "pref_cubatao_2026",
                title = "Prefeitura de Cubatão 2026",
                description = "Concurso da Prefeitura de Cubatão com vagas para diversas áreas municipais.",
                banca = "VUNESP",
                level = "Municipal",
                status = "Previsto",
                vacancies = 150,
                registrationStart = "Previsto",
                registrationEnd = "Previsto",
                examDate = "Previsto",
                salary = "R\$ 2.400 a R\$ 5.500",
                imageUrl = null,
                subjects = listOf("portugues", "raciocinio-logico", "informatica", "conhecimentos-especificos"),
                isFavorite = false,
                createdAt = now
            )
        )
        db.concursoDao().insertConcursos(concursos)

        // -- SUBJECTS e TOPICS para INSS --
        val inssSubjects = listOf(
            SubjectEntity("inss-direito-constitucional", "inss_2026", "Direito Constitucional",
                "Princípios fundamentais, direitos e garantias, organização do Estado", "⚖️", 5, 0, 1),
            SubjectEntity("inss-direito-administrativo", "inss_2026", "Direito Administrativo",
                "Administração pública, atos administrativos, servidores públicos", "📋", 5, 0, 2),
            SubjectEntity("inss-portugues", "inss_2026", "Português",
                "Gramática, interpretação de texto, redação oficial", "📝", 5, 0, 3),
            SubjectEntity("inss-raciocinio", "inss_2026", "Raciocínio Lógico",
                "Lógica proposicional, sequências, probabilidade", "🧮", 4, 0, 4),
            SubjectEntity("inss-previdenciario", "inss_2026", "Direito Previdenciário",
                "RGPS, benefícios, custeio da seguridade social", "🛡️", 6, 0, 5),
            SubjectEntity("inss-informatica", "inss_2026", "Informática",
                "Windows, Linux, Pacote Office, Internet, Segurança", "💻", 4, 0, 6)
        )
        db.subjectDao().insertSubjects(inssSubjects)

        // Topics - Direito Constitucional INSS
        val topics = listOf(
            TopicEntity("inss-dc-01", "inss-direito-constitucional", "inss_2026",
                "Princípios Fundamentais",
                """
# Princípios Fundamentais da Constituição Federal (Art. 1º ao 4º)

## Fundamentos da República (Art. 1º)

A República Federativa do Brasil é formada pela união indissolúvel dos Estados e Municípios e do Distrito Federal, constitui-se em Estado Democrático de Direito e tem como fundamentos:

| Inciso | Fundamento | Significado |
|--------|-----------|-------------|
| I | Soberania | Poder supremo do Estado no plano interno e igualdade no plano internacional |
| II | Cidadania | Participação do povo no exercício do poder |
| III | Dignidade da pessoa humana | Valor base de todo ordenamento jurídico |
| IV | Valores sociais do trabalho e livre iniciativa | Base da ordem econômica |
| V | Pluralismo político | Liberdade de ideias e partidos |

## Poderes da União (Art. 2º)

> São Poderes da União, independentes e harmônicos entre si, o Legislativo, o Executivo e o Judiciário.

## Objetivos Fundamentais (Art. 3º)

São objetivos da República Federativa do Brasil:

1. **Construir** uma sociedade livre, justa e solidária
2. **Garantir** o desenvolvimento nacional
3. **Erradicar** a pobreza e a marginalização e reduzir as desigualdades sociais e regionais
4. **Promover** o bem de todos, sem preconceitos de origem, raça, sexo, cor, idade e quaisquer outras formas de discriminação

> Dica CESPE: O Art. 3º fala de OBJETIVOS (verbos no infinitivo). O Art. 4º fala de RELAÇÕES INTERNACIONAIS. Não confunda!

## Relações Internacionais (Art. 4º)

A República Federativa do Brasil rege-se nas suas relações internacionais pelos seguintes princípios:

✅ Independência nacional
✅ Prevalência dos direitos humanos
✅ Autodeterminação dos povos
✅ Não-intervenção
✅ Igualdade entre os Estados
✅ Defesa da paz
✅ Solução pacífica dos conflitos
✅ Repúdio ao terrorismo e ao racismo
✅ Cooperação entre os povos para o progresso da humanidade
✅ Concessão de asilo político

> ⚠️ *Pegadinha típica:* A **independência nacional** é princípio de relações internacionais. Já a **cidadania** é fundamento.

## Questões CESPE típicas

1. *(CESPE) A dignidade da pessoa humana é um dos fundamentos da República Federativa do Brasil.* ✅ **Correto**
2. *(CESPE) São Poderes da União, dependentes e harmônicos entre si, o Legislativo, o Executivo e o Judiciário.* ❌ **Errado** (são INDEPENDENTES)
3. *(CESPE) Erradicar a pobreza é um dos objetivos fundamentais.* ✅ **Correto**

## Resumo Final (Flashcard Mental)

- **Fundamentos (Art. 1º):** SO-CI-DI-VA-PL 🧠 (Soberania, Cidadania, Dignidade, Valores, Pluralismo)
- **Poderes (Art. 2º):** LEG + EXEC + JUD = independentes e harmônicos
- **Objetivos (Art. 3º):** CON-GAR-ERRA-PRO 🔨 (Construir, Garantir, Erradicar, Promover)
- **R.I. (Art. 4º):** 🌍 dez princípios (não confunda com fundamentos!)
""",
                "Princípios fundamentais, poderes da União e objetivos da República com mnemônicos e pegadinhas das bancas.",
                45, "Fácil", false, 1),

            TopicEntity("inss-dc-02", "inss-direito-constitucional", "inss_2026",
                "Direitos e Garantias Fundamentais (Art. 5º)",
                """
# Direitos e Garantias Fundamentais - Art. 5º da CF

## Estrutura do Art. 5º

O Art. 5º contém **78 incisos** que tratam dos direitos e deveres individuais e coletivos. É o artigo mais cobrado em concurso!

## Classificação dos Direitos

| Dimensão | Característica | Exemplo |
|----------|---------------|---------|
| 1ª Geração | Liberdade (direitos civis/políticos) | Vida, liberdade, propriedade |
| 2ª Geração | Igualdade (direitos sociais) | Saúde, educação, trabalho |
| 3ª Geração | Fraternidade (direitos difusos) | Meio ambiente, paz, desenvolvimento |

## Principais Garantias

### Direito à Vida
- Proibição de pena de morte (salvo guerra declarada)
- Garantia de existência digna

### Direito à Igualdade
> "Todos são iguais perante a lei, sem distinção de qualquer natureza" (caput)

### Direito à Liberdade
- Liberdade de locomoção (ninguém será preso senão em flagrante ou por ordem judicial)
- Liberdade de pensamento e expressão
- Liberdade de crença religiosa

### Direito à Segurança
- Inviolabilidade do domicílio (só pode entrar com ordem judicial, salvo flagrante ou desastre - durante o dia)
- Inviolabilidade das comunicações (salvo por ordem judicial para investigação criminal)

### Direito de Propriedade
- É garantido, desde que atenda a função social
- Desapropriação mediante justa indenização

## Remédios Constitucionais (importante! 🚨)

| Remédio | Proteção | Quem pode |
|---------|----------|-----------|
| Habeas Corpus | Liberdade de locomoção | Qualquer pessoa (gratuito, sem advogado) |
| Mandado de Segurança | Direito líquido e certo | Pessoa física ou jurídica |
| Mandado de Injunção | Falta de norma regulamentadora | Qualquer pessoa |
| Habeas Data | Dados pessoais | Titular dos dados |
| Ação Popular | Ato lesivo ao patrimônio público | Cidadão eleitor |

> 💡 *Dica de ouro:* **Habeas Corpus** é gratuito e qualquer pessoa (inclusive analfabeta) pode impetrar. **Ação Popular** só cidadão eleitor pode propor.

## Questões Típicas CESPE

1. *(CESPE) O direito de propriedade é absoluto e não admite limitações.* ❌ **Errado** (deve atender função social)
2. *(CESPE) O mandado de segurança pode ser impetrado por qualquer pessoa física ou jurídica.* ✅ **Correto**
3. *(CESPE) À noite, a entrada em domicílio sem consentimento do morador depende de ordem judicial.* ✅ **Correto** (salvo flagrante/desastre que é só durante o dia)
4. *(CESPE) O habeas data garante o acesso a informações pessoais constantes de bancos de dados governamentais.* ✅ **Correto**

## Mnemônico - Remédios Constitucionais 🧠

**H A M H A** = Habeas Corpus, Ação Popular, Mandado de Segurança, Habeas Data, Mandado de Injunção
""",
                "Direitos individuais e coletivos, remédios constitucionais e dicas para as provas.",
                50, "Médio", false, 2)
        )
        db.topicDao().insertTopics(topics)

        // Questões de exemplo
        val questions = listOf(
            QuestionEntity(
                "inss-q1", "inss-dc-01", "inss_2026",
                "Conforme a Constituição Federal de 1988, assinale a alternativa que apresenta um dos fundamentos da República Federativa do Brasil:",
                listOf(
                    "A) Construir uma sociedade livre, justa e solidária",
                    "B) A dignidade da pessoa humana",
                    "C) Garantir o desenvolvimento nacional",
                    "D) Erradicar a pobreza e a marginalização"
                ),
                1,
                "A dignidade da pessoa humana é um dos fundamentos (Art. 1º, III). As demais alternativas são objetivos fundamentais do Art. 3º.",
                "CESPE",
                2024,
                "Fácil"
            ),
            QuestionEntity(
                "inss-q2", "inss-dc-01", "inss_2026",
                "São Poderes da União, independentes e harmônicos entre si:",
                listOf(
                    "A) O Legislativo, o Executivo e o Judiciário",
                    "B) O Executivo, o Judiciário e o Ministério Público",
                    "C) O Legislativo, o Executivo e o Tribunal de Contas",
                    "D) O Executivo, o Legislativo e a Defensoria Pública"
                ),
                0,
                "Art. 2º da CF: 'São Poderes da União, independentes e harmônicos entre si, o Legislativo, o Executivo e o Judiciário.'",
                "CESPE",
                2024,
                "Fácil"
            ),
            QuestionEntity(
                "inss-q3", "inss-dc-01", "inss_2026",
                "Assinale a opção que apresenta um princípio que rege a República Federativa do Brasil em suas relações internacionais:",
                listOf(
                    "A) Pluralismo político",
                    "B) Cidadania",
                    "C) Não-intervenção",
                    "D) Valores sociais do trabalho"
                ),
                2,
                "A não-intervenção é um princípio das relações internacionais (Art. 4º). Pluralismo político, cidadania e valores sociais do trabalho são fundamentos (Art. 1º).",
                "CESPE",
                2023,
                "Fácil"
            ),
            QuestionEntity(
                "inss-q4", "inss-dc-02", "inss_2026",
                "Acerca dos direitos e garantias fundamentais, assinale a opção correta:",
                listOf(
                    "A) O direito de propriedade é absoluto e incondicionado",
                    "B) É admitida a pena de morte no Brasil em caso de crime hediondo",
                    "C) A casa é asilo inviolável do indivíduo, ninguém nela podendo penetrar sem consentimento do morador, salvo em caso de flagrante delito ou desastre, ou para prestar socorro, ou, durante o dia, por determinação judicial",
                    "D) O mandado de segurança é gratuito e dispensada a assistência de advogado"
                ),
                2,
                "Art. 5º, XI - exatamente como descrito. A propriedade não é absoluta (deve ter função social). Pena de morte só em guerra declarada. Mandado de segurança exige advogado.",
                "CESPE",
                2024,
                "Médio"
            ),
            QuestionEntity(
                "inss-q5", "inss-dc-02", "inss_2026",
                "Qual remédio constitucional pode ser impetrado por qualquer pessoa, independentemente de advogado e de forma gratuita?",
                listOf(
                    "A) Mandado de Segurança",
                    "B) Habeas Corpus",
                    "C) Mandado de Injunção",
                    "D) Habeas Data"
                ),
                1,
                "O Habeas Corpus é o único remédio constitucional gratuito e que dispensa advogado, pois protege o direito mais fundamental: a liberdade de locomoção.",
                "CESPE",
                2024,
                "Fácil"
            ),
            QuestionEntity(
                "inss-q6", "inss-dc-02", "inss_2026",
                "A inviolabilidade das comunicações telefônicas, por ordem judicial, para fins de investigação criminal:",
                listOf(
                    "A) É absoluta, não admitindo qualquer exceção",
                    "B) Só é admitida para crimes dolosos",
                    "C) É admitida por ordem judicial, nas hipóteses e na forma que a lei estabelecer, para fins de investigação criminal ou instrução processual penal",
                    "D) Pode ser determinada por qualquer autoridade policial"
                ),
                2,
                "Art. 5º, XII - a inviolabilidade das comunicações telefônicas pode ser quebrada por ordem judicial, para fins de investigação criminal ou instrução processual penal.",
                "CESPE",
                2023,
                "Médio"
            ),
            QuestionEntity(
                "inss-q7", "inss-previdenciario", "inss_2026",
                "O Regime Geral de Previdência Social (RGPS) é de filiação:",
                listOf(
                    "A) Facultativa para empregados",
                    "B) Obrigatória para todos os trabalhadores urbanos e rurais",
                    "C) Obrigatória apenas para servidores públicos",
                    "D) Facultativa para menores de 16 anos"
                ),
                1,
                "O RGPS tem filiação obrigatória para todos os trabalhadores urbanos e rurais que exercem atividade remunerada (Art. 201 da CF).",
                "CESPE",
                2024,
                "Fácil"
            ),
            QuestionEntity(
                "inss-q8", "inss-portugues", "inss_2026",
                "Assinale a alternativa em que a concordância verbal está CORRETA:",
                listOf(
                    "A) Fazem cinco anos que ele trabalha no INSS",
                    "B) Haviam muitas pessoas na fila do atendimento",
                    "C) Havia muitos beneficiários aguardando atendimento",
                    "D) Devem haver soluções para o problema"
                ),
                2,
                "'Haver' no sentido de 'existir' é impessoal e fica no singular. O correto é 'Havia muitos beneficiários'.",
                "CESPE",
                2024,
                "Médio"
            ),
            QuestionEntity(
                "inss-q9", "inss-raciocinio", "inss_2026",
                "Considere a proposição: 'Se João é servidor público, então João tem estabilidade'. A negação dessa proposição é:",
                listOf(
                    "A) João não é servidor público e João tem estabilidade",
                    "B) João é servidor público e João não tem estabilidade",
                    "C) João não é servidor público ou João não tem estabilidade",
                    "D) Se João não é servidor público, então João não tem estabilidade"
                ),
                1,
                "A negação de P→Q é P ∧ ¬Q (o 'MANÉ' - Mantém a primeira E nega a segunda).",
                "CESPE",
                2024,
                "Médio"
            ),
            QuestionEntity(
                "inss-q10", "inss-previdenciario", "inss_2026",
                "O benefício de aposentadoria por tempo de contribuição exige, para homens, o tempo mínimo de contribuição de:",
                listOf(
                    "A) 30 anos",
                    "B) 35 anos",
                    "C) 25 anos",
                    "D) 20 anos"
                ),
                1,
                "Para homens, a aposentadoria por tempo de contribuição exige 35 anos de contribuição (pré-Reforma) ou 20 anos a partir da Reforma (se cumprir idade de 65 anos).",
                "CESPE",
                2024,
                "Fácil"
            )
        )
        db.questionDao().insertQuestions(questions)

        // -- SUBJECTS do Banco do Brasil --
        val bbSubjects = listOf(
            SubjectEntity("bb-portugues", "banco_brasil_2026", "Português",
                "Gramática, interpretação de textos, redação de correspondências oficiais", "📝", 4, 0, 1),
            SubjectEntity("bb-raciocinio", "banco_brasil_2026", "Raciocínio Lógico",
                "Estruturas lógicas, probabilidade, análise combinatória", "🧮", 3, 0, 2),
            SubjectEntity("bb-conhecimentos-bancarios", "banco_brasil_2026", "Conhecimentos Bancários",
                "Sistema financeiro nacional, mercado de capitais, produtos bancários", "🏦", 5, 0, 3),
            SubjectEntity("bb-informatica", "banco_brasil_2026", "Informática",
                "Excel, Word, sistemas operacionais, segurança da informação", "💻", 3, 0, 4),
            SubjectEntity("bb-matematica-financeira", "banco_brasil_2026", "Matemática Financeira",
                "Juros simples e compostos, descontos, séries de pagamentos", "💰", 3, 0, 5),
            SubjectEntity("bb-atendimento", "banco_brasil_2026", "Atendimento",
                "Qualidade no atendimento, ética, lei do superendividamento", "🤝", 2, 0, 6)
        )
        db.subjectDao().insertSubjects(bbSubjects)

        // ================================================================
        // CONCURSOS BAIXADA SANTISTA — MATÉRIAS BASEADAS EM EDITAIS REAIS
        // ================================================================

        // -- PREFEITURA DE SANTOS --
        val santosSubjects = listOf(
            SubjectEntity("st-portugues", "pref_santos_2026", "Português",
                "Compreensão textual, ortografia, classes gramaticais, sintaxe, concordância, regência, crase, pontuação (Edital Pref. Santos)", "📝", 6, 0, 1),
            SubjectEntity("st-informatica", "pref_santos_2026", "Informática",
                "Windows, pacote Office (Word, Excel, PPT), internet, segurança digital, redes sociais no serviço público", "💻", 4, 0, 2),
            SubjectEntity("st-raciocinio", "pref_santos_2026", "Raciocínio Lógico",
                "Estruturas lógicas, proposições, diagramas, análise combinatória, probabilidade, problemas aritméticos", "🧮", 4, 0, 3),
            SubjectEntity("st-direito-constitucional", "pref_santos_2026", "Direito Constitucional",
                "CF/88: Princípios fundamentais, direitos e garantias, administração pública, servidores públicos (Art. 37 a 41)", "⚖️", 5, 0, 4),
            SubjectEntity("st-direito-admin", "pref_santos_2026", "Direito Administrativo",
                "Atos administrativos, licitações (Lei 14.133/2021), servidores, improbidade, responsabilidade civil do Estado", "📋", 5, 0, 5),
            SubjectEntity("st-leg-municipal", "pref_santos_2026", "Legislação Municipal",
                "Lei Orgânica de Santos, Estatuto do Servidor, Plano de Carreira, regime jurídico municipal", "🏛️", 4, 0, 6),
            SubjectEntity("st-conhec-gerais", "pref_santos_2026", "Conhecimentos Gerais",
                "História e geografia de Santos, Baixada Santista, atualidades, economia regional, Porto de Santos", "📍", 4, 0, 7),
        )
        db.subjectDao().insertSubjects(santosSubjects)

        // -- TOPICS de Santos --
        val santosTopics = listOf(
            TopicEntity("st-top1", "st-portugues", "pref_santos_2026", "Compreensão e Interpretação Textual",
                "Leitura crítica de textos, figuras de linguagem, níveis de linguagem, paráfrase e inferências.", "Estudo de como extrair informações implícitas e explícitas de textos de concursos.", 30, "Médio", false, 1),
            TopicEntity("st-top2", "st-portugues", "pref_santos_2026", "Ortografia, Acentuação e Crase",
                "Novo acordo ortográfico, acentuação tônica e gráfica, uso do acento grave (crase).", "Domine as regras de acentuação e o uso correto da crase.", 25, "Médio", false, 2),
            TopicEntity("st-top3", "st-portugues", "pref_santos_2026", "Concordância e Regência",
                "Concordância nominal e verbal, regência verbal e nominal, casos especiais.", "Técnicas para acertar concordância e regência nas provas.", 30, "Difícil", false, 3),
            TopicEntity("st-top4", "st-informatica", "pref_santos_2026", "Windows e Pacote Office",
                "Windows 10/11, atalhos, Word (formatação, mala direta), Excel (fórmulas, tabelas, gráficos), PowerPoint.", "Aprenda os atalhos e funções mais cobrados.", 35, "Médio", false, 1),
            TopicEntity("st-top5", "st-informatica", "pref_santos_2026", "Internet e Segurança Digital",
                "Navegadores, e-mail, redes sociais institucionais, certificação digital, Lei Geral de Proteção de Dados (LGPD).", "Segurança da informação no serviço público.", 25, "Fácil", false, 2),
            TopicEntity("st-top6", "st-direito-constitucional", "pref_santos_2026", "Princípios Fundamentais e Direitos Individuais",
                "Art. 1º ao 5º da CF/88: fundamentos da República, direitos e garantias fundamentais, remédios constitucionais.", "Base do Direito Constitucional para qualquer concurso municipal.", 35, "Médio", false, 1),
            TopicEntity("st-top7", "st-direito-constitucional", "pref_santos_2026", "Administração Pública na CF",
                "Art. 37 ao 41: princípios da adm. pública, servidores públicos, estabilidade, acumulação de cargos.", "Tópico mais cobrado em concursos municipais.", 30, "Difícil", false, 2),
            TopicEntity("st-top8", "st-direito-admin", "pref_santos_2026", "Licitações (Lei 14.133/2021)",
                "Modalidades de licitação, fases, dispensa, inexigibilidade, contrato administrativo, sanções.", "Nova lei de licitações — conteúdo essencial.", 40, "Difícil", false, 1),
            TopicEntity("st-top9", "st-leg-municipal", "pref_santos_2026", "Lei Orgânica de Santos",
                "Organização municipal, competências, tributos municipais, servidores, processo legislativo municipal.", "Estude a estrutura básica do município de Santos.", 30, "Médio", false, 1),
            TopicEntity("st-top10", "st-conhec-gerais", "pref_santos_2026", "Porto de Santos e Economia Regional",
                "História do Porto, relevância econômica, impactos ambientais, saneamento, turismo regional.", "Conhecimento da maior cidade da Baixada Santista.", 25, "Médio", false, 1),
        )
        db.topicDao().insertTopics(santosTopics)

        // -- QUESTÕES da Baixada Santista --
        val bsQuestions = listOf(
            QuestionEntity("bs-q1", "st-top1", "pref_santos_2026",
                "Em relação à interpretação de textos, assinale a alternativa CORRETA:",
                listOf("A) A paráfrase consiste em repetir exatamente as mesmas palavras do texto original",
                       "B) A inferência é uma informação que pode ser deduzida a partir do texto",
                       "C) A intertextualidade ocorre apenas quando há citação explícita de outro autor",
                       "D) A ambiguidade textual sempre configura um erro de redação"),
                1, "A inferência é uma conclusão baseada em pistas textuais, informação que está implícita e precisa ser deduzida pelo leitor.", "VUNESP", 2024, "Médio"),

            QuestionEntity("bs-q2", "st-top3", "pref_santos_2026",
                "Assinale a alternativa correta quanto à concordância verbal:",
                listOf("A) Fazem três anos que trabalho na prefeitura",
                       "B) Havia muitas pessoas na reunião",
                       "C) Mais de um funcionário faltaram ao trabalho",
                       "D) Haviam muitos candidatos inscritos"),
                1, "'Haver' no sentido de 'existir' é impessoal e fica no singular: 'Havia muitas pessoas'.", "VUNESP", 2024, "Médio"),

            QuestionEntity("bs-q3", "st-top4", "pref_santos_2026",
                "No Microsoft Excel, a função que retorna o maior valor de um intervalo de células é:",
                listOf("A) MÍNIMO", "B) MÁXIMO", "C) MAIOR", "D) TOP"),
                1, "A função =MÁXIMO(intervalo) retorna o maior valor numérico dentro do intervalo selecionado.", "VUNESP", 2024, "Fácil"),

            QuestionEntity("bs-q4", "st-top6", "pref_santos_2026",
                "São fundamentos da República Federativa do Brasil, EXCETO:",
                listOf("A) Soberania", "B) Dignidade da pessoa humana",
                       "C) União indissolúvel dos estados e municípios",
                       "D) Pluralismo político"),
                2, "A União Indissolúvel é um dos fundamentos do Estado Democrático de Direito (Art. 1º, parágrafo único). Os fundamentos do Art. 1º são: soberania, cidadania, dignidade da pessoa humana, valores sociais do trabalho e da livre iniciativa, e pluralismo político.", "VUNESP", 2023, "Médio"),

            QuestionEntity("bs-q5", "st-top7", "pref_santos_2026",
                "De acordo com o Art. 37 da CF/88, NÃO é princípio da Administração Pública:",
                listOf("A) Legalidade", "B) Impessoalidade", "C) Continuidade", "D) Eficiência"),
                2, "O Art. 37 da CF/88 elenca os princípios: Legalidade, Impessoalidade, Moralidade, Publicidade e Eficiência (LIMPE). Continuidade não é um dos princípios explícitos.", "VUNESP", 2024, "Fácil"),

            QuestionEntity("bs-q6", "st-top8", "pref_santos_2026",
                "Segundo a Lei 14.133/2021, a modalidade de licitação para contratação de obras e serviços de engenharia acima de R$ 4,8 milhões é:",
                listOf("A) Pregão", "B) Concorrência", "C) Tomada de Preços", "D) Leilão"),
                1, "A Concorrência é a modalidade obrigatória para obras acima de R$ 4,8 milhões (critérios da nova lei). O pregão é para bens e serviços comuns.", "FGV", 2024, "Difícil"),

            QuestionEntity("bs-q7", "st-top10", "pref_santos_2026",
                "O Porto de Santos é considerado economicamente importante para o Brasil por qual motivo principal?",
                listOf("A) Maior porto pesqueiro do Atlântico Sul",
                       "B) Maior complexo portuário da América Latina, responsável por 28% da balança comercial",
                       "C) Único porto brasileiro com capacidade para navios de grande porte",
                       "D) Principal terminal de passageiros do país"),
                1, "O Porto de Santos é o maior complexo portuário da América Latina, responsável por cerca de 28% das trocas comerciais do Brasil.", "VUNESP", 2023, "Fácil"),

            QuestionEntity("bs-q8", "st-top9", "pref_santos_2026",
                "A Lei Orgânica do Município de Santos estabelece que o Poder Legislativo municipal é exercido pela:",
                listOf("A) Assembleia Municipal", "B) Câmara Municipal", "C) Prefeitura", "D) Conselho Municipal"),
                1, "O Poder Legislativo municipal é exercido pela Câmara Municipal, composta por vereadores eleitos, conforme a Lei Orgânica e a CF/88.", "VUNESP", 2024, "Fácil"),

            QuestionEntity("bs-q9", "st-top6", "pref_santos_2026",
                "O remédio constitucional cabível para proteger direito líquido e certo não amparado por habeas corpus ou habeas data é:",
                listOf("A) Ação Popular", "B) Mandado de Injunção", "C) Mandado de Segurança", "D) Habeas Data"),
                2, "O Mandado de Segurança (Art. 5º, LXIX da CF) protege direito líquido e certo não amparado por HC ou HD.", "VUNESP", 2024, "Médio"),

            QuestionEntity("bs-q10", "st-top2", "pref_santos_2026",
                "Assinale a alternativa em que o uso da crase está CORRETO:",
                listOf("A) Entreguei o documento à ele", "B) Fui à praia ontem",
                       "C) Ele se referiu àquela senhora", "D) Paguei à vista"),
                2, "A crase em 'àquela' é correta: preposição 'a' + pronome demonstrativo 'aquela'.", "VUNESP", 2024, "Médio"),

            // Questões sobre São Vicente
            QuestionEntity("bs-q11", "st-top1", "pref_saovicente_2026",
                "São Vicente, primeira vila do Brasil, foi fundada em:",
                listOf("A) 1500", "B) 1532", "C) 1554", "D) 1565"),
                1, "São Vicente foi fundada por Martim Afonso de Sousa em 1532, sendo a primeira vila do Brasil.", "VUNESP", 2023, "Fácil"),

            QuestionEntity("bs-q12", "st-top10", "pref_santos_2026",
                "A Região Metropolitana da Baixada Santista é composta por quantos municípios?",
                listOf("A) 7", "B) 8", "C) 9", "D) 10"),
                2, "A Região Metropolitana da Baixada Santista é composta por 9 municípios: Santos, São Vicente, Praia Grande, Cubatão, Guarujá, Peruíbe, Itanhaém, Mongaguá e Bertioga.", "VUNESP", 2024, "Médio"),
        )
        db.questionDao().insertQuestions(bsQuestions)
    }
}