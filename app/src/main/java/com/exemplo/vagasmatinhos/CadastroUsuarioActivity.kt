package com.exemplo.vagasmatinhos

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CadastroUsuarioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_usuario)

        val editNome = findViewById<EditText>(R.id.editNomeReg)
        val editEmail = findViewById<EditText>(R.id.editEmailReg)
        val editSenha = findViewById<EditText>(R.id.editSenhaReg)
        val radioCandidato = findViewById<RadioButton>(R.id.radioCandidato)
        val btnSalvar = findViewById<Button>(R.id.btnSalvarRegistro)
        val dbHelper = BancoDadosHelper(this)

        btnSalvar.setOnClickListener {
            val nome = editNome.text.toString().trim()
            val email = editEmail.text.toString().trim()
            val senha = editSenha.text.toString().trim()
            val tipo = if (radioCandidato.isChecked) "candidato" else "empresa"

            if (nome.isNotEmpty() && email.isNotEmpty() && senha.isNotEmpty()) {
                val sucesso = dbHelper.cadastrarUsuario(email, senha, tipo, nome)
                if (sucesso) {
                    Toast.makeText(this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Erro: E-mail já cadastrado.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}