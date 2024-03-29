package com.example.lab_2

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.room.Room
import com.example.notas.Notas
import com.example.notas.NotasDAOImpl
import com.example.notas.NotasDatabase
import java.util.Calendar

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AT_Notas : AppCompatActivity() {
    private lateinit var notasDAOImpl: NotasDAOImpl

    // Dentro da classe AT_Notas
    inner class NotaAdapter(context: Context, notas: List<Notas>) :
        ArrayAdapter<Notas>(context, R.layout.list_item_nota, notas) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val itemView = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_nota, parent, false)

            val currentNota = getItem(position)
            val tvDescricao = itemView.findViewById<TextView>(R.id.tvDescricao)
            val tvData = itemView.findViewById<TextView>(R.id.tvData)
            val btnExcluir = itemView.findViewById<Button>(R.id.btnExcluir)

            // Define a descrição e a data da nota
            tvDescricao.text = currentNota?.descricao
            tvData.text = currentNota?.data

            // Define o clique do botão de exclusão
            btnExcluir.setOnClickListener {
                currentNota?.let {
                    // Execute a exclusão em uma coroutine
                    GlobalScope.launch(Dispatchers.IO) {
                        notasDAOImpl.remover(it)
                        // Atualiza a interface do usuário na thread principal
                        withContext(Dispatchers.Main) {
                            remove(it)
                        }
                    }
                }
            }

            return itemView
        }
    }

    // Dentro do método onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_at_notas)

        val db = Room.databaseBuilder(
            applicationContext,
            NotasDatabase::class.java,
            "notas-db"
        ).build()

        val notasDAO = db.notasDao()
        notasDAOImpl = NotasDAOImpl(notasDAO) // Inicializa notasDAOImpl aqui

        val btnAdicionarNota = findViewById<Button>(R.id.btnAdicionarNota)
        val listaNotas = findViewById<ListView>(R.id.listaNotas)
        val etDescricao = findViewById<EditText>(R.id.etDescricao)
        val datePicker = findViewById<DatePicker>(R.id.datePicker)

        btnAdicionarNota.setOnClickListener {
            val descricao = etDescricao.text.toString()
            val dataSelecionada = Calendar.getInstance()
            dataSelecionada.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
            val data = dataSelecionada.timeInMillis

            if (descricao.length >= 5) {
                val novaNota = Notas(id = 0, descricao = descricao, data = data.toString())
                // Execute a operação de inserção em uma coroutine
                GlobalScope.launch(Dispatchers.IO) {
                    val idNotaInserida = notasDAOImpl.inserir(novaNota)
                    // Atualize a interface do usuário na thread principal
                    if (idNotaInserida != -1L) {
                        val todasNotas = notasDAOImpl.buscarTodas()
                        withContext(Dispatchers.Main) {
                            (listaNotas.adapter as ArrayAdapter<Notas>).clear()
                            (listaNotas.adapter as ArrayAdapter<Notas>).addAll(todasNotas)
                        }
                    } else {
                        // Tratar falha de inserção, se necessário
                    }
                }
            } else {
                Toast.makeText(this, "A descrição deve ter pelo menos 5 caracteres", Toast.LENGTH_SHORT).show()
            }
        }

        GlobalScope.launch(Dispatchers.IO) {
            // Busca todas as notas do banco de dados
            val todasNotas = notasDAOImpl.buscarTodas()
            withContext(Dispatchers.Main) {
                val adapter = ArrayAdapter<Notas>(
                    this@AT_Notas,
                    R.layout.list_item_nota,
                    R.id.tvDescricao,
                    todasNotas
                )
                listaNotas.adapter = adapter
            }
        }
    }
}
