package com.exemplo.vagasmatinhos

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAbrirVaga: Button
    private lateinit var btnAbrirCandidato: Button
    private lateinit var btnSair: Button
    private lateinit var txtSaudacao: TextView
    private lateinit var spinnerFiltro: Spinner
    private lateinit var dbHelper: BancoDadosHelper

    private var tipoUsuario = "candidato"
    private var userId = ""
    private var categoriaSelecionada = "Todas"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerViewVagas)
        btnAbrirVaga = findViewById(R.id.btnAbrirCadastroVaga)
        btnAbrirCandidato = findViewById(R.id.btnAbrirCadastroCandidato)
        btnSair = findViewById(R.id.btnSair)
        txtSaudacao = findViewById(R.id.txtSaudacaoUsuario)
        spinnerFiltro = findViewById(R.id.spinnerFiltroCategoria)
        dbHelper = BancoDadosHelper(this)

        recyclerView.layoutManager = LinearLayoutManager(this)

        tipoUsuario = intent.getStringExtra("TIPO_USUARIO") ?: "candidato"
        val nomeUsuario = intent.getStringExtra("NOME_USUARIO") ?: "Usuário"
        userId = intent.getStringExtra("USER_ID") ?: ""

        txtSaudacao.text = "Logado como: $nomeUsuario ($tipoUsuario)"

        // Configurando Spinner de Filtro
        val filtros = arrayOf("Todas", "Comércio", "Imobiliária", "Serviços", "Hotel", "Outros")
        val adapterFiltro = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, filtros)
        spinnerFiltro.adapter = adapterFiltro

        spinnerFiltro.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                categoriaSelecionada = filtros[position]
                carregarVagas()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        // Ajustando o título conforme o perfil
        val txtTituloSecao = findViewById<TextView>(R.id.txtTituloSecao) // Certifique-se de ter este ID no activity_main.xml se quiser dinamizar o texto

        if (tipoUsuario == "empresa") {
            btnAbrirVaga.visibility = View.VISIBLE
            btnAbrirCandidato.visibility = View.GONE
            spinnerFiltro.visibility = View.GONE
            // txtTituloSecao.text = "Minhas Vagas Cadastradas"
        } else {
            btnAbrirVaga.visibility = View.GONE
            btnAbrirCandidato.visibility = View.VISIBLE
            spinnerFiltro.visibility = View.VISIBLE
            // txtTituloSecao.text = "Vagas Disponíveis:"
        }

        btnAbrirVaga.setOnClickListener {
            val intent = Intent(this, CadastrarVagaActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        btnAbrirCandidato.setOnClickListener {
            val novaIntent = Intent(this, CadastroCandidatoActivity::class.java)
            novaIntent.putExtra("USER_ID", userId) // Usa a variável userId da MainActivity
            startActivity(novaIntent)
        }

        btnSair.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        carregarVagas()
    }

    private fun carregarVagas() {
        val listaVagas = if (tipoUsuario == "empresa") {
            dbHelper.retornarVagas(empresaId = userId) // Apenas vagas desta empresa
        } else {
            dbHelper.retornarVagas(categoriaFiltro = categoriaSelecionada) // Filtrado por categoria
        }

        recyclerView.adapter = VagaAdapter(listaVagas) { vagaSelecionada ->
            val intent = Intent(this, DetalheVagaActivity::class.java).apply {
                putExtra("VAGA_ID", vagaSelecionada.id)
                putExtra("TITULO", vagaSelecionada.titulo)
                putExtra("EMPRESA", vagaSelecionada.empresa)
                putExtra("CATEGORIA", vagaSelecionada.categoria)
                putExtra("SALARIO", vagaSelecionada.salario)
                putExtra("DESCRICAO", vagaSelecionada.descricao)
                putExtra("TIPO_USUARIO", tipoUsuario)
                putExtra("USER_ID", userId)
            }
            startActivity(intent)
        }
    }
}