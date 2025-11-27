package com.ifpr.androidapptemplate.service

import com.ifpr.androidapptemplate.rpg.data.Goblin
import com.ifpr.androidapptemplate.rpg.data.Atributos
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.GenericTypeIndicator
import kotlinx.coroutines.tasks.await
import java.lang.IllegalStateException

object FirebaseService {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val userDatabaseReference: DatabaseReference
        get() {
            val userId = auth.currentUser?.uid
                ?: throw IllegalStateException("Usuário não autenticado.")
            return FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("characters")
        }

    // ---------------------------
    // SALVAR GOBLIN
    // ---------------------------
    suspend fun salvarGoblin(goblin: Goblin) {
        val novoNoRef = userDatabaseReference.push()
        val goblinComId = goblin.copy(id = novoNoRef.key ?: "erro_id")
        novoNoRef.setValue(goblinComId).await()
    }

    // ---------------------------
    // CARREGAR GOBLINS
    // ---------------------------
    suspend fun carregarFichas(): List<Goblin> {
        val snapshot = userDatabaseReference.get().await()

        if (!snapshot.exists()) return emptyList()

        val t = object : GenericTypeIndicator<Map<String, Goblin>>() {}
        val goblinsMap = snapshot.getValue(t) ?: return emptyList()

        // --- NORMALIZAÇÃO PARA EVITAR CRASH ---
        return goblinsMap.values.map { g ->
            g.copy(
                atributos = g.atributos ?: Atributos(),
                nivel = g.nivel ?: 1,
                ferimentos = g.ferimentos ?: 0,
                equipamento = g.equipamento ?: emptyList()
            )
        }
    }
}
