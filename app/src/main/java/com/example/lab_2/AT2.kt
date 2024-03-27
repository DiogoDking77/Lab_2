package com.example.lab_2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class AT2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_at2)

        val btnFecharAT2 = findViewById<Button>(R.id.btn_fechar_at2)
        btnFecharAT2.setOnClickListener {
            finish()
        }

        // Exiba os dados do aluno
        val tvDadosAluno = findViewById<TextView>(R.id.tv_dados_aluno)
        val nomeAluno = resources.getString(R.string.aluno_nome)
        val cursoAluno = resources.getString(R.string.aluno_curso)
        val numeroAluno = resources.getString(R.string.aluno_numero)
        val dadosAluno = "$nomeAluno\n$cursoAluno\n$numeroAluno"
        tvDadosAluno.text = dadosAluno
    }
}
