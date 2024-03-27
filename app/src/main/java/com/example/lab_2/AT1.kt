package com.example.lab_2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.room.Room
import com.example.lab_2.ui.theme.Lab_2Theme
import com.example.notas.NotasDAOImpl
import com.example.notas.NotasDatabase

class AT1 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_at1)

        // Inicialize o banco de dados e o DAO
        val db = Room.databaseBuilder(applicationContext, NotasDatabase::class.java, "notas-db").build()
        val notasDAO = db.notasDao()

        // Crie uma instância de NotasDAOImpl
        val notasDAOImpl = NotasDAOImpl(notasDAO)

        // Restante do código da sua atividade
        val btnAbrirAT2 = findViewById<Button>(R.id.btn_abrir_at2)
        btnAbrirAT2.setOnClickListener {
            val intent = Intent(this, AT2::class.java)
            startActivity(intent)
        }

        // Encontrar e configurar o novo botão para abrir AT_Notas
        val btnAbrirATNotas = findViewById<Button>(R.id.btn_abrir_at3)
        btnAbrirATNotas.setOnClickListener {
            val intent = Intent(this, AT_Notas::class.java)
            startActivity(intent)
        }
    }
}



