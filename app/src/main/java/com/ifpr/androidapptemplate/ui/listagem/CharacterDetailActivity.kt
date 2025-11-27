package com.ifpr.androidapptemplate.ui.listagem

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ifpr.androidapptemplate.baseclasses.RpgCharacter
import com.ifpr.androidapptemplate.databinding.ActivityCharacterDetailBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CharacterDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCharacterDetailBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCharacterDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicialização do Firebase
        auth = FirebaseAuth.getInstance()
        // ⚠️ INICIALIZANDO RTDB
        database = FirebaseDatabase.getInstance()

        // Obter o ID do personagem (que é a chave/nó do RTDB) passado pela lista
        val characterId = intent.getStringExtra("CHARACTER_ID")

        if (characterId.isNullOrEmpty()) {
            Toast.makeText(this, "ID do personagem não encontrado.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Carregar os dados completos
        loadCharacterDetails(characterId)
    }

    // ⚠️ FUNÇÃO CORRIGIDA PARA RTDB
    private fun loadCharacterDetails(characterId: String) {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "Usuário não autenticado. Faça login novamente.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        binding.progressBarDetail.visibility = View.VISIBLE

        // Referência do nó no RTDB: users/{userId}/characters/{characterId}
        val characterRef = database.getReference("users")
            .child(userId)
            .child("characters")
            .child(characterId)

        characterRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.progressBarDetail.visibility = View.GONE

                if (snapshot.exists()) {
                    // Mapeia o nó do RTDB para a sua data class RpgCharacter
                    val character = snapshot.getValue(RpgCharacter::class.java)

                    if (character != null) {
                        displayCharacter(character)
                    } else {
                        Toast.makeText(this@CharacterDetailActivity, "Erro ao ler a ficha.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@CharacterDetailActivity, "Ficha de personagem não encontrada.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progressBarDetail.visibility = View.GONE
                Toast.makeText(this@CharacterDetailActivity, "Erro ao buscar detalhes: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun displayCharacter(character: RpgCharacter) {
        // ⚠️ CORRIGIDO: Usando os nomes das variáveis com underscore
        binding.tvDetailName.text = character.nome_personagem
        binding.tvDetailRaceClass.text = "Raça: ${character.raca_personagem} | Classe: ${character.classe_personagem}"
        binding.tvDetailConcept.text = character.conceito_personagem
        binding.tvDetailHistory.text = character.historia_completa

        // Formata e exibe a data de criação
        character.data_criacao?.let { timestamp ->
            val date = Date(timestamp)
            val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))
            binding.tvDetailCreatedAt.text = "Criado em: ${formatter.format(date)}"
        } ?: run {
            binding.tvDetailCreatedAt.text = "Data de criação indisponível"
        }
    }
}