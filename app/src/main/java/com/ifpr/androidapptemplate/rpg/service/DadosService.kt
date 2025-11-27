package com.ifpr.androidapptemplate.service

import com.ifpr.androidapptemplate.rpg.data.*
import com.ifpr.androidapptemplate.data.*
import kotlin.random.Random

object DadosService {

    fun rolarD6(n: Int = 1): List<Int> {
        return (1..n).map { Random.nextInt(1, 7) }
    }

    fun criarNovoGoblin(): Goblin {

        // --- NOME ---
        val resultadosNome = rolarD6(2)
        val nomeFinal = TABELA_NOMES[resultadosNome[1] - 1][resultadosNome[0] - 1]

        // --- OCUPAÇÃO ---
        val ocupacaoData = TABELA_OCUPACOES[rolarD6().first() - 1]

        // --- DESCRITOR ---
        val descritorData = TABELA_DESCRITORES[rolarD6().first() - 1]

        // --- CARACTERÍSTICA ---
        val resultadosCarac = rolarD6(2)
        val caracteristica = TABELA_CARACTERISTICAS[resultadosCarac[1] - 1][resultadosCarac[0] - 1]

        // --- ATRIBUTOS BASE ---
        val atributos = Atributos()

        // Aplicar modificadores da Ocupação
        aplicarMod(atributos, ocupacaoData.mod)

        // Aplicar modificadores do Descritor
        if (descritorData.mod.requerEscolha) {
            // SUPIMPA → jogador escolherá o atributo depois
            // então não modifica nada agora
        } else {
            aplicarMod(atributos, descritorData.mod)
        }

        return Goblin(
            nome = nomeFinal,
            ocupacao = ocupacaoData.nome,
            descritor = descritorData.nome,
            caracteristica = caracteristica,
            atributos = atributos,
            equipamento = ocupacaoData.equipamento
        )
    }

    // --- Função utilitária para aplicar modificadores ---
    private fun aplicarMod(atributos: Atributos, mod: Modificador) {
        atributos.combate += mod.combate
        atributos.habilidade += mod.habilidade
        atributos.nocao += mod.nocao
        atributos.vitalidade += mod.vitalidade
    }
}
