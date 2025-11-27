package com.ifpr.androidapptemplate.data
// Estrutura para Modificadores (Simplifica a aplicação dos bônus/penalidades)
data class Modificador(
    val combate: Int = 0,
    val habilidade: Int = 0,
    val nocao: Int = 0,
    val vitalidade: Int = 0,
    val requerEscolha: Boolean = false // Para o Descritor 'Supimpa'
)

// Tabela 1: Nomes (Matriz 6x6)
// D2 para linha (indice 0-5), D1 para coluna (indice 0-5)
val TABELA_NOMES = listOf(
    listOf("Plaft", "Plin", "Tik", "Tok", "Bash", "Cricri"),
    listOf("Glub", "Tim", "Ranço", "Yhaa", "Vrum", "Aiaiai"),
    listOf("Crash", "Zzzz", "Sussa", "Bibi", "Boom", "Bum"),
    listOf("Spray", "Cringe", "Sopa", "Ovo", "Ban", "Nhack"),
    listOf("Bing", "Riso", "Slash", "Coff", "Ugh", "Sniff"),
    listOf("[Última coisa que comeu]", "[Última coisa que comeu]", "[Última coisa que comeu]",
        "[Inverta seu nome]", "[Inverta seu nome]", "[Inverta seu nome]")
)

// Estruturas auxiliares para Ocupação e Descritor
data class OcupacaoData(val nome: String, val mod: Modificador, val equipamento: List<String>)
data class DescritorData(val nome: String, val mod: Modificador)

// Tabela 2: Ocupações e Modificadores (usa 1D6)
val TABELA_OCUPACOES = listOf(
    OcupacaoData("Mercenário", Modificador(combate = 1), listOf("Arma branca simples")),
    OcupacaoData("Caçador", Modificador(combate = 1), listOf("Arco e 10 flechas", "Ração de Viagem")),
    OcupacaoData("Gatuno", Modificador(habilidade = 1), listOf("Adagas de arremesso", "Corda")),
    OcupacaoData("Líder", Modificador(vitalidade = 1), listOf("Bandana suja", "Pedaço de papel e lápis")),
    OcupacaoData("Incendiário", Modificador(vitalidade = 1), listOf("Óleo de fogo (1)", "Pavio e Pederneira")),
    OcupacaoData("Bruxo", Modificador(nocao = 1), listOf("Amuleto esquisito", "Livro de Magias (Vazio)"))
)

// Tabela 3: Descritores e Modificadores (usa 1D6)
val TABELA_DESCRITORES = listOf(
    DescritorData("Covarde", Modificador(combate = -1)),
    DescritorData("Atrapalhado", Modificador(habilidade = -1)),
    DescritorData("Tapado", Modificador(nocao = -1)),
    DescritorData("Fracote", Modificador(vitalidade = -1)),
    DescritorData("Medíocre", Modificador()),
    DescritorData("Supimpa", Modificador(requerEscolha = true))
)

// Tabela 4: Característica (Matriz 6x6)
val TABELA_CARACTERISTICAS = listOf(
    listOf("Bomba-relógio", "Minicabeça", "Apêndice extra", "Poros fedidos", "Verdura", "[Role 2 vezes]"),
    listOf("Cinzento", "Cabeção", "Orelha extra", "Pintas", "Minion", "Fosforescente"),
    listOf("Amaldiçoado", "Linguão", "Nariz extra", "Listras", "Galináceo", "Colorido"),
    listOf("Tom bélico", "Olho gigante", "Olhos extras", "Pompom", "Peixoso", "Amarelo"),
    listOf("Flutulência", "Pés gigantes", "Braço extra", "Chifre", "Felino", "Azul"),
    listOf("[Role 2 vezes]", "Mão gigante", "Cabeça extra", "Cicatrizes", "Aracnídeo", "Vermelho")
)