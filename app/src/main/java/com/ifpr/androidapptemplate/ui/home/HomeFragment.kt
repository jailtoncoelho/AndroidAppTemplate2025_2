package com.ifpr.androidapptemplate.ui.home

// IMPORTS NECESSÁRIOS PARA A NOVA LÓGICA
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.ifpr.androidapptemplate.R
import com.ifpr.androidapptemplate.baseclasses.Tesouro // << RENOMEIE Item.kt PARA Tesouro.kt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class HomeFragment : Fragment() {

    // --- 1. ONDE DECLARAR AS VARIÁVEIS ---
    // Variáveis para geolocalização (você já tinha)
    private lateinit var currentAddressTextView: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: Location? = null // <- Guardar a localização atual

    // Variáveis para os componentes da UI do formulário
    private lateinit var nomeTesouroEditText: EditText
    private lateinit var descricaoTesouroEditText: EditText
    private lateinit var previewImageView: ImageView
    private var imagemSelecionadaUri: Uri? = null // <- Guardar a URI da imagem

    // Launcher para obter a imagem da galeria
    private val pegarImagemLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imagemSelecionadaUri = result.data?.data
            previewImageView.setImageURI(imagemSelecionadaUri) // Mostra a imagem na tela
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 2
    }

    // --- 2. ONDE CONFIGURAR A TELA (onCreateView) ---
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Infla o novo layout do formulário
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Inicializa as permissões e a localização (lógica que você já tinha)
        requestNotificationPermission()
        inicializaGerenciamentoLocalizacao(view)

        // Vincula as variáveis da UI com os componentes do XML
        nomeTesouroEditText = view.findViewById(R.id.editText_nome_tesouro)
        descricaoTesouroEditText = view.findViewById(R.id.editText_descricao_tesouro)
        previewImageView = view.findViewById(R.id.imageView_preview)
        val btnAdicionarFoto = view.findViewById<Button>(R.id.button_adicionar_foto)
        val btnSalvarTesouro = view.findViewById<Button>(R.id.button_salvar_tesouro)

        // --- 3. ONDE COLOCAR A LÓGICA DOS BOTÕES ---
        btnAdicionarFoto.setOnClickListener {
            abrirGaleria()
        }

        btnSalvarTesouro.setOnClickListener {
            salvarTesouro()
        }

        return view
    }

    // --- 4. ONDE COLOCAR A LÓGICA PARA ABRIR A GALERIA ---
    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        pegarImagemLauncher.launch(intent)
    }

    // --- 5. ONDE COLOCAR A LÓGICA PRINCIPAL PARA SALVAR O TESOURO ---
    private fun salvarTesouro() {
        val nome = nomeTesouroEditText.text.toString().trim()
        val descricao = descricaoTesouroEditText.text.toString().trim()
        val usuario = FirebaseAuth.getInstance().currentUser

        // Validações
        if (nome.isEmpty() || descricao.isEmpty()) {
            Toast.makeText(context, "Nome e descrição são obrigatórios.", Toast.LENGTH_SHORT).show()
            return
        }
        if (imagemSelecionadaUri == null) {
            Toast.makeText(context, "Selecione uma foto para o tesouro.", Toast.LENGTH_SHORT).show()
            return
        }
        if (currentLocation == null) {
            Toast.makeText(context, "Aguardando localização...", Toast.LENGTH_SHORT).show()
            return
        }
        if (usuario == null) {
            Toast.makeText(context, "Erro de autenticação. Faça login novamente.", Toast.LENGTH_SHORT).show()
            return
        }

        val snackbar = Snackbar.make(requireView(), "Salvando tesouro, por favor aguarde...", Snackbar.LENGTH_INDEFINITE)
        snackbar.show()

        // Faz o upload da imagem para o Firebase Storage
        val storageRef = FirebaseStorage.getInstance().reference
        val idImagem = UUID.randomUUID().toString()
        val imagemRef = storageRef.child("tesouros/$idImagem.jpg")

        imagemRef.putFile(imagemSelecionadaUri!!)
            .addOnSuccessListener {
                // Se o upload der certo, pega a URL de download
                imagemRef.downloadUrl.addOnSuccessListener { url ->
                    // Cria o objeto Tesouro com todos os dados
                    val tesouroId = FirebaseDatabase.getInstance().reference.child("tesouros").push().key ?: ""
                    val novoTesouro = Tesouro(
                        id = tesouroId,
                        nome = nome,
                        descricao = descricao,
                        imageUrl = url.toString(),
                        latitude = currentLocation!!.latitude,
                        longitude = currentLocation!!.longitude,
                        endereco = currentAddressTextView.text.toString(),
                        criadoPorUid = usuario.uid,
                        criadoPorNome = usuario.displayName ?: "Anônimo",
                        timestamp = System.currentTimeMillis()
                    )

                    // Salva o objeto no Realtime Database
                    FirebaseDatabase.getInstance().getReference("tesouros")
                        .child(tesouroId)
                        .setValue(novoTesouro)
                        .addOnSuccessListener {
                            snackbar.dismiss()
                            Toast.makeText(context, "Tesouro salvo com sucesso!", Toast.LENGTH_LONG).show()
                            // Limpa o formulário
                            nomeTesouroEditText.text.clear()
                            descricaoTesouroEditText.text.clear()
                            previewImageView.setImageURI(null)
                        }
                        .addOnFailureListener {
                            snackbar.dismiss()
                            Toast.makeText(context, "Falha ao salvar no banco de dados: ${it.message}", Toast.LENGTH_LONG).show()
                        }
                }
            }
            .addOnFailureListener {
                snackbar.dismiss()
                Toast.makeText(context, "Falha no upload da imagem: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }


    // --- 6. CÓDIGO DE SUPORTE (MAIORIA JÁ EXISTIA) ---

    // Ajuste em getCurrentLocation para guardar a localização
    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) { return }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                this.currentLocation = location // <- GUARDA A LOCALIZAÇÃO
                displayAddress(location)
            }
        }
    }

    private fun inicializaGerenciamentoLocalizacao(view: View) {
        currentAddressTextView = view.findViewById(R.id.currentAddressTextView)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission()
        } else {
            getCurrentLocation()
        }
    }

    private fun displayAddress(location: Location) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                val address = addresses?.firstOrNull()?.getAddressLine(0) ?: "Endereço não encontrado"
                withContext(Dispatchers.Main) {
                    currentAddressTextView.text = address
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { currentAddressTextView.text = "Erro ao obter endereço." }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_REQUEST_CODE)
            }
        }
    }

    private fun requestLocationPermission() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation()
                } else {
                    Snackbar.make(requireView(), "Permissão de localização negada.", Snackbar.LENGTH_LONG).show()
                }
            }
            NOTIFICATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Permissão de notificação concedida!", Toast.LENGTH_SHORT).show()
                } else {
                    Snackbar.make(requireView(), "Você não receberá alertas importantes.", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }
}
