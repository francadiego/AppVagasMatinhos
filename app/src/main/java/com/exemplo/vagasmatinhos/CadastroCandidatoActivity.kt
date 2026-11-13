package com.exemplo.vagasmatinhos

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class CadastroCandidatoActivity : AppCompatActivity() {

    private lateinit var editNome: EditText
    private lateinit var editTelefone: EditText
    private lateinit var editEndereco: EditText
    private lateinit var editPretensao: EditText
    private lateinit var editExperiencia: EditText
    private lateinit var dbHelper: BancoDadosHelper
    private var userId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_candidato)

        editNome = findViewById(R.id.editNomeCandidato)
        editTelefone = findViewById(R.id.editTelefone)
        editEndereco = findViewById(R.id.editEndereco)
        editPretensao = findViewById(R.id.editPretensao)
        editExperiencia = findViewById(R.id.editExperiencia)
        val btnSalvar = findViewById<Button>(R.id.btnSalvarCadastro)
        val btnExcluir = findViewById<Button>(R.id.btnExcluirCurriculo)

        dbHelper = BancoDadosHelper(this)
        userId = intent.getStringExtra("USER_ID") ?: ""

        btnSalvar.setOnClickListener {
            val nome = editNome.text.toString().trim()
            val telefone = editTelefone.text.toString().trim()
            val endereco = editEndereco.text.toString().trim()
            val pretensao = editPretensao.text.toString().trim()
            val experiencia = editExperiencia.text.toString().trim()

            if (userId.isEmpty()) {
                Toast.makeText(this, "Erro: Usuário não identificado.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sucesso = dbHelper.atualizarCurriculoCompleto(userId, nome, telefone, endereco, pretensao, experiencia)
            if (sucesso) {
                Toast.makeText(this, "Currículo salvo com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Erro ao salvar currículo.", Toast.LENGTH_SHORT).show()
            }
        }

        btnExcluir.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Excluir Currículo")
                .setMessage("Deseja apagar todas as informações do seu currículo?")
                .setPositiveButton("Sim") { _, _ ->
                    dbHelper.atualizarCurriculoCompleto(userId, "", "", "", "", "")
                    editNome.setText("")
                    editTelefone.setText("")
                    editEndereco.setText("")
                    editPretensao.setText("")
                    editExperiencia.setText("")
                    Toast.makeText(this, "Currículo limpo com sucesso!", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Não", null)
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        carregarDadosCurriculo()
    }

    private fun carregarDadosCurriculo() {
        if (userId.isNotEmpty()) {
            val dados = dbHelper.retornarCurriculoCompleto(userId)
            if (dados != null) {
                editNome.setText(dados["nome"] ?: "")
                editTelefone.setText(dados["telefone"] ?: "")
                editEndereco.setText(dados["endereco"] ?: "")
                editPretensao.setText(dados["pretensao"] ?: "")
                editExperiencia.setText(dados["experiencia"] ?: "")
            }
        }
    }
}