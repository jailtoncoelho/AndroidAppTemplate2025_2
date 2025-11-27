package com.ifpr.androidapptemplate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.setupWithNavController
import androidx.navigation.fragment.NavHostFragment // <--- IMPORT CRÍTICO
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.ifpr.androidapptemplate.R // Importa o R para IDs de recursos

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        // 1. Encontra a barra de navegação inferior
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        // 2. ENCONTRA O NAV HOST FRAGMENT PELO GERENCIADOR DE FRAGMENTS (Abordagem mais segura)
        // O ID 'nav_host_fragment_activity_main' DEVE existir no activity_main.xml!
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment

        // 3. Encontra o controlador de navegação a partir do host
        val navController = navHostFragment.navController

        // 4. LIGA A BOTTOM BAR AO NAV CONTROLLER. ISSO FAZ OS ÍCONES FUNCIONAREM!
        navView.setupWithNavController(navController)
    }

    /**
     * Sobrescreve o botão Voltar para sair do aplicativo (se logado) ou manter o padrão.
     */
    override fun onBackPressed() {
        val user = auth.currentUser

        // Se o usuário estiver logado e estiver na tela principal
        if (user != null) {
            // Em vez de voltar, simula a ação HOME, saindo do app
            moveTaskToBack(true)
        } else {
            // Caso contrário, mantém o comportamento padrão
            super.onBackPressed()
        }
    }
}