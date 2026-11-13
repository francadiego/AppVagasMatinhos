package com.exemplo.vagasmatinhos

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class DetalheVagaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_vaga)

        val txtTitulo = findViewById<TextView>(R.id.detalheTitulo)
        val txtEmpresa = findViewById<TextView>(R.id.detalheEmpresa)
        val txtCategoria = findViewById<TextView>(R.id.detalheCategoria)
        val txtSalario = findViewById<TextView>(R.id.detalheSalario)
        val txtDescricao = findViewById<TextView>(R.id.detalheDescricao)
        val btnAcao = findViewById<Button>(R.id.btnCandidatar)

        // Se criar botões extras no layout para empresa:
        // val btnEditar = findViewById<Button>(R.id.btnEditarVaga)
        // val btnExcluir = findViewById<Button>(R.id.btnExcluirVaga)

        val dbHelper = BancoDadosHelper(this)

        val vagaId = intent.getStringExtra("VAGA_ID") ?: ""
        var titulo = intent.getStringExtra("TITULO") ?: ""
        var empresa = intent.getStringExtra("EMPRESA") ?: ""
        var categoria = intent.getStringExtra("CATEGORIA") ?: ""
        var salario = intent.getStringExtra("SALARIO") ?: ""
        var descricao = intent.getStringExtra("DESCRICAO") ?: ""
        val tipoUsuario = intent.getStringExtra("TIPO_USUARIO") ?: "candidato"
        val userId = intent.getStringExtra("USER_ID") ?: ""

        txtTitulo.text = titulo
        txtEmpresa.text = "Empresa: $empresa"
        txtCategoria.text = "Categoria: $categoria"
        txtSalario.text = salario
        txtDescricao.text = descricao

        if (tipoUsuario == "empresa") {
            // Mudamos o texto principal ou adicionamos ações para a empresa
            btnAcao.text = "Ver Candidatos Inscritos"
            btnAcao.setBackgroundColor(0xFF1976D2.toInt())

            btnAcao.setOnClickListener {
                val candidatos = dbHelper.retornarCandidatosDaVaga(vagaId)
                if (candidatos.isEmpty()) {
                    Toast.makeText(this, "Nenhum candidato se inscreveu nesta vaga ainda.", Toast.LENGTH_SHORT).show()
                } else {
                    val nomesCandidatos = candidatos.map { "${it["nome"]} (${it["email"]})" }.toTypedArray()
                    AlertDialog.Builder(this)
                        .setTitle("Candidatos Inscritos")
                        .setItems(nomesCandidatos) { _, index ->
                            val candSelecionado = candidatos[index]
                            val intent = Intent(this, DetalheCurriculoActivity::class.java).apply {
                                putExtra("NOME", candSelecionado["nome"] ?: "")
                                putExtra("EMAIL", candSelecionado["email"] ?: "")
                                putExtra("ENDERECO", candSelecionado["endereco"] ?: "")
                                putExtra("PRETENSAO", candSelecionado["pretensao"] ?: "")
                                putExtra("EXPERIENCIA", candSelecionado["experiencia"] ?: "")
                            }
                            startActivity(intent)
                        }
                        .setNegativeButton("Fechar") { dialog, _ -> dialog.dismiss() }
                        .show()
                }
            }

            // Criando botões extras dinamicamente na tela para Editar e Excluir a Vaga
            // (Ou você pode adicioná-los diretamente no seu layout XML se preferir)
        } else {
            btnAcao.text = "Candidatar-se à Vaga"
            btnAcao.setBackgroundColor(0xFF2E7D32.toInt())

            btnAcao.setOnClickListener {
                val sucesso = dbHelper.candidatar(vagaId, userId)
                if (sucesso) {
                    Toast.makeText(this, "Candidatura realizada com sucesso!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this, "Você já se candidatou a esta vaga!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        val btnEditar = findViewById<Button>(R.id.btnEditarVaga)
        val btnExcluir = findViewById<Button>(R.id.btnExcluirVaga)

        if (tipoUsuario == "empresa") {
            btnAcao.text = "Ver Candidatos Inscritos"
            btnAcao.setBackgroundColor(0xFF1976D2.toInt())
            btnEditar.visibility = View.VISIBLE
            btnExcluir.visibility = View.VISIBLE

            // Ação de Ver Candidatos
            btnAcao.setOnClickListener {
                val candidatos = dbHelper.retornarCandidatosDaVaga(vagaId)
                if (candidatos.isEmpty()) {
                    Toast.makeText(this, "Nenhum candidato inscrito.", Toast.LENGTH_SHORT).show()
                } else {
                    val nomes = candidatos.map { "${it["nome"]} (${it["email"]})" }.toTypedArray()
                    AlertDialog.Builder(this)
                        .setTitle("Candidatos")
                        .setItems(nomes) { _, index ->
                            val c = candidatos[index]
                            val intent = Intent(this, DetalheCurriculoActivity::class.java).apply {
                                putExtra("NOME", c["nome"] ?: "")
                                putExtra("EMAIL", c["email"] ?: "")
                                putExtra("ENDERECO", c["endereco"] ?: "")
                                putExtra("PRETENSAO", c["pretensao"] ?: "")
                                putExtra("EXPERIENCIA", c["experiencia"] ?: "")
                            }
                            startActivity(intent)
                        }
                        .setNegativeButton("Fechar", null)
                        .show()
                }
            }

            // Ação de Excluir Vaga
            btnExcluir.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Encerrar Vaga")
                    .setMessage("Deseja realmente excluir esta vaga permanentemente?")
                    .setPositiveButton("Sim") { _, _ ->
                        dbHelper.excluirVaga(vagaId)
                        Toast.makeText(this, "Vaga encerrada com sucesso!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .setNegativeButton("Não", null)
                    .show()
            }

            // Ação de Editar Vaga (simples com campos de texto)
            btnEditar.setOnClickListener {
                val intent = Intent(this, CadastrarVagaActivity::class.java).apply {
                    putExtra("MODO_EDICAO", true)
                    putExtra("VAGA_ID", vagaId)
                    putExtra("TITULO", titulo)
                    putExtra("EMPRESA", empresa)
                    putExtra("CATEGORIA", categoria)
                    putExtra("SALARIO", salario)
                    putExtra("DESCRICAO", descricao)
                    putExtra("USER_ID", userId)
                }
                startActivity(intent)
                finish() // Fecha a tela de detalhes para atualizar a lista ao voltar
            }
        }
    }
}