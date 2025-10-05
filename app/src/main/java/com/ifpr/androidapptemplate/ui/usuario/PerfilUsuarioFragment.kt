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

class PerfilUsuarioFragment : Fragment() {

    private lateinit var userProfileImageView: ImageView
    private lateinit var registerNameEditText: EditText
    private lateinit var registerEmailEditText: EditText
    private lateinit var registerEnderecoEditText: EditText
    private lateinit var registerTelefoneEditText: EditText
    private lateinit var updateProfileButton: Button
    private lateinit var signOutButton: Button
    private lateinit var usersReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_perfil_usuario, container, false)

        auth = FirebaseAuth.getInstance()
        usersReference = FirebaseDatabase.getInstance().getReference("users")

        userProfileImageView = view.findViewById(R.id.userProfileImageView)
        registerNameEditText = view.findViewById(R.id.registerNameEditText)
        registerEmailEditText = view.findViewById(R.id.registerEmailEditText)
        registerEnderecoEditText = view.findViewById(R.id.registerEnderecoEditText)
        registerTelefoneEditText = view.findViewById(R.id.registerTelefoneEditText)
        updateProfileButton = view.findViewById(R.id.updateProfileButton)
        signOutButton = view.findViewById(R.id.signOutButton)

        val user = auth.currentUser
        if (user != null) {
            registerEmailEditText.setText(user.email)
            registerEmailEditText.isEnabled = false
            registerNameEditText.setText(user.displayName)
            loadProfileImage(user)
        }

        updateProfileButton.setOnClickListener { updateUser() }
        signOutButton.setOnClickListener { signOut() }

        recuperarDadosUsuario()

        return view
    }

    private fun loadProfileImage(user: FirebaseUser) {
        Glide.with(this)
            .load(user.photoUrl)
            .placeholder(R.drawable.ic_profile_black_24dp)
            .error(R.drawable.ic_profile_black_24dp)
            .into(userProfileImageView)
    }

    private fun updateUser() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(context, "Nenhum usuario autenticado.", Toast.LENGTH_SHORT).show()
            return
        }

        val name = registerNameEditText.text.toString().trim()
        val endereco = registerEnderecoEditText.text.toString().trim()
        val telefone = registerTelefoneEditText.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(context, "Informe o nome para atualizar.", Toast.LENGTH_SHORT).show()
            return
        }

        updateProfile(user, name, endereco, telefone)
    }

    private fun recuperarDadosUsuario() {
        val user = auth.currentUser ?: return

        usersReference.child(user.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val usuario = snapshot.getValue(Usuario::class.java) ?: return

                    usuario.nome?.takeIf { it.isNotBlank() }?.let { registerNameEditText.setText(it) }
                    usuario.email?.takeIf { it.isNotBlank() }?.let { registerEmailEditText.setText(it) }
                    registerEnderecoEditText.setText(usuario.endereco.orEmpty())
                    registerTelefoneEditText.setText(usuario.telefone.orEmpty())
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("PerfilUsuarioFragment", "Erro ao recuperar dados: ${error.message}")
                    Toast.makeText(context, "Falha ao carregar dados do usuario.", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun updateProfile(user: FirebaseUser, displayName: String, endereco: String, telefone: String) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .build()

        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveUserToDatabase(
                        Usuario(
                            key = user.uid,
                            nome = displayName,
                            email = user.email,
                            endereco = endereco,
                            telefone = telefone
                        )
                    )
                    Toast.makeText(context, "Perfil atualizado com sucesso.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Nao foi possivel atualizar o perfil.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserToDatabase(usuario: Usuario) {
        val key = usuario.key
        if (key.isNullOrEmpty()) {
            Log.e("PerfilUsuarioFragment", "Chave do usuario ausente ao salvar.")
            Toast.makeText(context, "Erro ao salvar os dados do usuario.", Toast.LENGTH_SHORT).show()
            return
        }

        usersReference.child(key)
            .setValue(usuario)
            .addOnFailureListener { error ->
                Log.e("PerfilUsuarioFragment", "Erro ao salvar dados: ${error.message}")
                Toast.makeText(context, "Falha ao salvar dados no banco.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun signOut() {
        auth.signOut()
        Toast.makeText(context, "Logout realizado com sucesso!", Toast.LENGTH_SHORT).show()
        requireActivity().finish()
    }
}
