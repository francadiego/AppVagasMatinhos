package com.exemplo.vagasmatinhos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val editEmail = findViewById<EditText>(R.id.editLoginEmail)
        val editSenha = findViewById<EditText>(R.id.editLoginSenha)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnIrParaCadastro = findViewById<Button>(R.id.btnIrParaCadastro)
        val dbHelper = BancoDadosHelper(this)

        btnLogin.setOnClickListener {
            val email = editEmail.text.toString().trim()
            val senha = editSenha.text.toString().trim()

            if (email.isNotEmpty() && senha.isNotEmpty()) {
                val usuario = dbHelper.validarLogin(email, senha)
                if (usuario != null) {
                    Toast.makeText(this, "Bem-vindo, ${usuario["nome"]}!", Toast.LENGTH_SHORT).show()

                    // Direciona para a tela principal enviando o tipo de usuário
                    val intent = Intent(this, MainActivity::class.java).apply {
                        putExtra("TIPO_USUARIO", usuario["tipo"]) // "empresa" ou "candidato"
                        putExtra("NOME_USUARIO", usuario["nome"])
                        putExtra("USER_ID", usuario["id"]) // Passa o ID do banco
                    }
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "E-mail ou senha inválidos!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            }
        }

        btnIrParaCadastro.setOnClickListener {
            startActivity(Intent(this, CadastroUsuarioActivity::class.java))
        }
    }
}