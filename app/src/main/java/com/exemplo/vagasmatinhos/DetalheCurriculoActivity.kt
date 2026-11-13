package com.exemplo.vagasmatinhos

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetalheCurriculoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhe_curriculo)

        val txtNome = findViewById<TextView>(R.id.txtNomeCandidato)
        val txtEmail = findViewById<TextView>(R.id.txtEmailCandidato)
        val txtEndereco = findViewById<TextView>(R.id.txtEnderecoCandidato)
        val txtPretensao = findViewById<TextView>(R.id.txtPretensaoCandidato)
        val txtExperiencia = findViewById<TextView>(R.id.txtExperienciaCandidato)

        txtNome.text = intent.getStringExtra("NOME") ?: ""
        txtEmail.text = "E-mail: ${intent.getStringExtra("EMAIL")}"
        txtEndereco.text = "Endereço: ${intent.getStringExtra("ENDERECO")}"
        txtPretensao.text = "Pretensão Salarial: ${intent.getStringExtra("PRETENSAO")}"
        txtExperiencia.text = intent.getStringExtra("EXPERIENCIA") ?: "Nenhuma informação informada."
    }
}