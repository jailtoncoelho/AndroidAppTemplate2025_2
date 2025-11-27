package com.ifpr.androidapptemplate.ui.listagem

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.ifpr.androidapptemplate.R
import com.ifpr.androidapptemplate.service.FirebaseService
import kotlinx.coroutines.launch

class CharacterListFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private lateinit var adapter: GoblinAdapter

    private lateinit var authListener: AuthStateListener
    private val auth = FirebaseAuth.getInstance()

    // =====================================================
    // CRIA O LISTENER AQUI
    // =====================================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authListener = AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                // Se o usuário está logado, tenta carregar as fichas
                carregarFichasSalvas()
                Log.d("AuthListener", "Usuário autenticado: ${user.uid}")
            } else {
                // Se o usuário está deslogado, limpa a lista se o adapter já foi inicializado
                if (::adapter.isInitialized)
                    adapter.updateList(emptyList())
                Log.d("AuthListener", "Usuário deslogado.")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos o layout diretamente
        return inflater.inflate(R.layout.fragment_character_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 🎯 CORREÇÃO AQUI: Usando o ID correto do XML (recyclerViewCharacters)
        recyclerView = view.findViewById(R.id.recyclerViewCharacters)

        if (recyclerView == null) {
            Log.e("FragmentError", "RecyclerView recyclerViewCharacters NÃO encontrado no layout.")
            Toast.makeText(requireContext(), "Erro fatal de layout. ID da RecyclerView incorreto.", Toast.LENGTH_LONG).show()
            // Evita NullPointerException se a RecyclerView não for encontrada
            return
        }

        recyclerView!!.layoutManager = LinearLayoutManager(requireContext())
        // Inicializa o adapter
        adapter = GoblinAdapter(emptyList())
        recyclerView!!.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        // Adiciona o listener de autenticação
        auth.addAuthStateListener(authListener)
    }

    override fun onStop() {
        super.onStop()
        // Remove o listener de autenticação para evitar vazamento de memória
        auth.removeAuthStateListener(authListener)
    }

    private fun carregarFichasSalvas() {
        // Verifica se o adapter já foi inicializado pelo onViewCreated
        if (!::adapter.isInitialized) return

        // Usa a coroutine scope do Fragment para garantir que o trabalho pare quando a view for destruída
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Chama o serviço do Firebase (assume que FirebaseService.carregarFichas() retorna List<Ficha>)
                val lista = FirebaseService.carregarFichas()
                adapter.updateList(lista)

                if (lista.isEmpty()) {
                    Toast.makeText(requireContext(), "Nenhuma ficha encontrada.", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Log.e("Firebase", "Erro ao carregar fichas:", e)
                Toast.makeText(requireContext(), "Erro ao carregar fichas.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Limpa a referência da RecyclerView para evitar vazamentos de memória de views
        recyclerView = null
    }
}