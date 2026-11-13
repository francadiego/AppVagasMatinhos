package com.exemplo.vagasmatinhos

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CadastrarVagaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastrar_vaga)

        val editTitulo = findViewById<EditText>(R.id.editTituloVaga)
        val editEmpresa = findViewById<EditText>(R.id.editEmpresaVaga)
        val spinnerCategoria = findViewById<Spinner>(R.id.spinnerCategoriaVaga)
        val editSalario = findViewById<EditText>(R.id.editSalarioVaga)
        val editDescricao = findViewById<EditText>(R.id.editDescricaoVaga)
        val btnSalvar = findViewById<Button>(R.id.btnSalvarVaga)

        val dbHelper = BancoDadosHelper(this)
        val userId = intent.getStringExtra("USER_ID") ?: ""

        // Verifica se veio do botão "Editar Vaga"
        val modoEdicao = intent.getBooleanExtra("MODO_EDICAO", false)
        val vagaId = intent.getStringExtra("VAGA_ID") ?: ""

        val categorias = arrayOf("Comércio", "Imobiliária", "Serviços", "Hotel", "Outros")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categorias)
        spinnerCategoria.adapter = adapter

        if (modoEdicao) {
            btnSalvar.text = "Salvar Alterações"
            editTitulo.setText(intent.getStringExtra("TITULO") ?: "")
            editEmpresa.setText(intent.getStringExtra("EMPRESA") ?: "")
            editSalario.setText(intent.getStringExtra("SALARIO") ?: "")
            editDescricao.setText(intent.getStringExtra("DESCRICAO") ?: "")

            // Seleciona a categoria atual no Spinner
            val categoriaAtual = intent.getStringExtra("CATEGORIA") ?: ""
            val pos = categorias.indexOf(categoriaAtual)
            if (pos >= 0) spinnerCategoria.setSelection(pos)
        }

        btnSalvar.setOnClickListener {
            val titulo = editTitulo.text.toString().trim()
            val empresa = editEmpresa.text.toString().trim()
            val categoria = spinnerCategoria.selectedItem.toString()
            val salario = editSalario.text.toString().trim()
            val descricao = editDescricao.text.toString().trim()

            if (titulo.isEmpty() || empresa.isEmpty()) {
                Toast.makeText(this, "Preencha o Título e a Empresa!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (modoEdicao) {
                val sucesso = dbHelper.atualizarVaga(vagaId, titulo, empresa, categoria, salario, descricao)
                if (sucesso) {
                    Toast.makeText(this, "Vaga atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Erro ao atualizar vaga.", Toast.LENGTH_SHORT).show()
                }
            } else {
                val novaVaga = Vaga("", titulo, empresa, categoria, salario, descricao)
                val sucesso = dbHelper.inserirVaga(novaVaga, userId)
                if (sucesso) {
                    Toast.makeText(this, "Vaga publicada com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Erro ao salvar vaga.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}