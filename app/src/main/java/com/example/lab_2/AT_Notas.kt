package com.example.lab_2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.room.Room
import com.example.notas.Notas
import com.example.notas.NotasDAOImpl
import com.example.notas.NotasDatabase
import java.util.Calendar

class AT_Notas : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_at_notas)

        val db = Room.databaseBuilder(
            applicationContext,
            NotasDatabase::class.java,
            "notas-db"
        ).build()

        val notasDAO = db.notasDao() // Acesso ao DAO através do banco de dados
        val notasDAOImpl = NotasDAOImpl(notasDAO) // Agora você está passando o DAO, não o banco de dados

        val btnAdicionarNota = findViewById<Button>(R.id.btnAdicionarNota)
        val listaNotas = findViewById<ListView>(R.id.listaNotas)
        val etDescricao = findViewById<EditText>(R.id.etDescricao)
        val datePicker = findViewById<DatePicker>(R.id.datePicker)

        // Adiciona um listener de clique para o botão de adicionar nota
        btnAdicionarNota.setOnClickListener {
            // Obtenha a descrição da nova nota do EditText
            val descricao = etDescricao.text.toString()

            // Obtenha a data da nova nota do DatePicker
            val dataSelecionada = Calendar.getInstance()
            dataSelecionada.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
            val data = dataSelecionada.timeInMillis

            // Verifique se a descrição tem pelo menos 5 caracteres
            if (descricao.length >= 5) {
                // Crie um objeto Notas com os detalhes fornecidos
                val novaNota = Notas(id = 0, descricao = descricao, data = data.toString()) // Converta o Long para String

                // Em seguida, chame o método inserir do seu DAO para adicionar a nota ao banco de dados
                val idNotaInserida = notasDAOImpl.inserir(novaNota)

                // Verifica se a inserção foi bem-sucedida
                if (idNotaInserida != -1L) {
                    // Se a inserção for bem-sucedida, atualize a lista de notas na ListView
                    val todasNotas = notasDAOImpl.buscarTodas()
                    (listaNotas.adapter as ArrayAdapter<Notas>).clear()
                    (listaNotas.adapter as ArrayAdapter<Notas>).addAll(todasNotas)
                } else {
                    // Se a inserção falhar, exiba uma mensagem de erro ou faça qualquer tratamento necessário
                }
            } else {
                // Se a descrição não atender aos requisitos mínimos, exiba uma mensagem de erro
                Toast.makeText(this, "A descrição deve ter pelo menos 5 caracteres", Toast.LENGTH_SHORT).show()
            }
        }

        // Aqui você precisa recuperar todas as notas do banco de dados
        val todasNotas = notasDAOImpl.buscarTodas()

        // Cria um ArrayAdapter para exibir as notas na ListView
        val adapter = ArrayAdapter<Notas>(this, android.R.layout.simple_list_item_1, todasNotas)

        // Define o adaptador para a ListView
        listaNotas.adapter = adapter
    }
}


