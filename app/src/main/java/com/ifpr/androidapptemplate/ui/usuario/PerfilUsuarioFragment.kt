package com.ifpr.androidapptemplate.ui.usuario

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ifpr.androidapptemplate.R
import com.ifpr.androidapptemplate.databinding.FragmentPerfilUsuarioBinding

class PerfilUsuarioFragment : Fragment() {

    private var _binding: FragmentPerfilUsuarioBinding? = null

    private lateinit var userProfileImageView: ImageView
    private lateinit var registerNameEditText: EditText
    private lateinit var registerEmailEditText: EditText
    private lateinit var usersReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_perfil_usuario, container, false)

        // Inicializa o Firebase Auth
        auth = FirebaseAuth.getInstance()

        userProfileImageView = view.findViewById(R.id.userProfileImageView)
        registerNameEditText = view.findViewById(R.id.registerNameEditText)
        registerEmailEditText = view.findViewById(R.id.registerEmailEditText)

        try {
            usersReference = FirebaseDatabase.getInstance().getReference("users")
        } catch (e: Exception) {
            Log.e("DatabaseReference", "Erro ao obter referência para o Firebase DatabaseReference", e)
            // Trate o erro conforme necessário, por exemplo:
            Toast.makeText(context, "Erro ao acessar o Firebase DatabaseReference", Toast.LENGTH_SHORT).show()
        }

        // Acessar currentUser
        val user = auth.currentUser

        if (user != null) {
            registerEmailEditText.isEnabled = false
        }

        user?.let {
            // Exibe a foto do perfil usando a biblioteca Glide
            Glide.with(this).load(it.photoUrl).into(userProfileImageView)
        }

        return view
    }

    private fun signOut() {
        auth.signOut()
        Toast.makeText(
            context,
            "Logout realizado com sucesso!",
            Toast.LENGTH_SHORT
        ).show()

        requireActivity().finish()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Exibe os dados do usuario logado, se disponivel

        // Acessar currentUser
        var userFirebase = auth.currentUser
        if(userFirebase != null){
            registerNameEditText.setText(userFirebase.displayName)
            registerEmailEditText.setText(userFirebase.email)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
