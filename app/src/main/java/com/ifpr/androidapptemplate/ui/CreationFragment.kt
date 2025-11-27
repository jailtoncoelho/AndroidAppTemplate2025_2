package com.ifpr.androidapptemplate.ui.creation // Adapte este pacote se necessário

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.ifpr.androidapptemplate.R // Importa o R para IDs de recursos
import com.ifpr.androidapptemplate.rpg.data.Goblin
import com.ifpr.androidapptemplate.service.DadosService
import com.ifpr.androidapptemplate.service.FirebaseService
import kotlinx.coroutines.launch

class CreationFragment : Fragment() {

    // Referências de UI (Usamos lateinit para inicializar em onViewCreated)
    private lateinit var btnCriarFicha: Button
    private lateinit var tvNome: TextView
    private lateinit var tvOcupacao: TextView
    private lateinit var tvDescritor: TextView
    private lateinit var tvCaracteristica: TextView
    private lateinit var tvCombate: TextView
    private lateinit var tvHabilidade: TextView
    private lateinit var tvNocao: TextView
    private lateinit var tvVitalidade: TextView
    private lateinit var tvEquipamento: TextView // Adicionado para o equipamento

    // 1. Infla o layout do Fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_creation, container, false)
    }

    // 2. Inicializa as Views e Adiciona Listeners
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // findViewById AGORA FUNCIONA, pois é chamado no View correta!
        btnCriarFicha = view.findViewById(R.id.btn_criar_ficha)
        tvNome = view.findViewById(R.id.tv_nome_goblin)
        tvOcupacao = view.findViewById(R.id.tv_ocupacao_goblin)
        tvDescritor = view.findViewById(R.id.tv_descritor_goblin)
        tvCaracteristica = view.findViewById(R.id.tv_caracteristica_goblin)
        tvCombate = view.findViewById(R.id.tv_combate_valor)
        tvHabilidade = view.findViewById(R.id.tv_habilidade_valor)
        tvNocao = view.findViewById(R.id.tv_nocao_valor)
        tvVitalidade = view.findViewById(R.id.tv_vitalidade_valor)
        tvEquipamento = view.findViewById(R.id.tv_equipamento_goblin)

        // Ligar a função ao clique do botão
        btnCriarFicha.setOnClickListener {
            criarESalvarNovoGoblin()
        }
    }

    // 3. Lógica de Criação (Movida do MainActivity)
    private fun criarESalvarNovoGoblin() {
        // Cria a ficha (Síncrono)
        val novaFicha = DadosService.criarNovoGoblin()

        // Exibe o resultado da rolagem na UI
        atualizarUI(novaFicha)

        // Salva no Firebase (Assíncrono, requer Corrotina)
        // Usamos viewLifecycleOwner.lifecycleScope em Fragments
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                FirebaseService.salvarGoblin(novaFicha)
                Toast.makeText(requireContext(), "Ficha salva no Firebase!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("Firebase", "Erro ao salvar Goblin: ${e.message}")
                Toast.makeText(requireContext(), "Erro ao salvar: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // 4. Função para Atualizar a UI
    private fun atualizarUI(ficha: Goblin) {
        tvNome.text = "${ficha.nome}"
        tvDescritor.text = "(${ficha.descritor})"
        tvOcupacao.text = ficha.ocupacao
        tvCaracteristica.text = ficha.caracteristica

        tvCombate.text = ficha.atributos.combate.toString()
        tvHabilidade.text = ficha.atributos.habilidade.toString()
        tvNocao.text = ficha.atributos.nocao.toString()
        tvVitalidade.text = ficha.atributos.vitalidade.toString()

        tvEquipamento.text = ficha.equipamento.joinToString(", ")
    }
}