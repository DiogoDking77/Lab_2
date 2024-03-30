package com.example.lab_2

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.room.Room
import com.example.notas.Notas
import com.example.notas.NotasDAOImpl
import com.example.notas.NotasDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AT_Notas : AppCompatActivity() {
    private lateinit var notasDAOImpl: NotasDAOImpl
    private lateinit var adapter: NotaAdapter
    private var isInputVisible = false// Declaração do adaptador

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
        val timePicker = findViewById<TimePicker>(R.id.timePicker)
        val DescriptionLabel = findViewById<TextView>(R.id.tvDescricaoLabel)
        val DataLabel = findViewById<TextView>(R.id.tvDataLabel)
        val btnFecharAT2 = findViewById<Button>(R.id.btnVoltarAT1)
        btnFecharAT2.setOnClickListener {
            finish()
        }

        etDescricao.visibility = View.GONE
        datePicker.visibility = View.GONE
        DescriptionLabel.visibility = View.GONE
        DataLabel.visibility = View.GONE
        timePicker.visibility = View.GONE
        btnAdicionarNota.visibility = View.GONE

        // Configura o adaptador
        adapter = NotaAdapter(this, R.layout.list_item_nota, mutableListOf())
        listaNotas.adapter = adapter

        btnAdicionarNota.setOnClickListener {
            val descricao = etDescricao.text.toString()
            val dataSelecionada = Calendar.getInstance()
            dataSelecionada.set(datePicker.year, datePicker.month, datePicker.dayOfMonth, timePicker.hour, timePicker.minute)
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
                            adapter.clear()
                            adapter.addAll(todasNotas)
                            etDescricao.setText("")
                            etDescricao.visibility = View.GONE
                            datePicker.visibility = View.GONE
                            DescriptionLabel.visibility = View.GONE
                            DataLabel.visibility = View.GONE
                            timePicker.visibility = View.GONE
                            btnAdicionarNota.visibility = View.GONE
                            listaNotas.visibility = View.VISIBLE
                            isInputVisible = false
                        }
                    } else {
                        // Tratar falha de inserção, se necessário
                    }
                }
            } else {
                Toast.makeText(this, "A descrição deve ter pelo menos 5 caracteres", Toast.LENGTH_SHORT).show()
            }
        }

        val btnToggleView = findViewById<Button>(R.id.btnToggleView)
        btnToggleView.setOnClickListener {
            if (!isInputVisible) {
                // Mostrar campos de entrada e esconder lista de notas
                etDescricao.visibility = View.VISIBLE
                datePicker.visibility = View.VISIBLE
                timePicker.visibility = View.VISIBLE
                DescriptionLabel.visibility = View.VISIBLE
                DataLabel.visibility = View.VISIBLE
                btnAdicionarNota.visibility = View.VISIBLE
                listaNotas.visibility = View.GONE
                isInputVisible = true
            } else {
                // Esconder campos de entrada e mostrar lista de notas
                etDescricao.visibility = View.GONE
                datePicker.visibility = View.GONE
                DescriptionLabel.visibility = View.GONE
                DataLabel.visibility = View.GONE
                timePicker.visibility = View.GONE
                btnAdicionarNota.visibility = View.GONE
                listaNotas.visibility = View.VISIBLE
                isInputVisible = false
            }
        }

        GlobalScope.launch(Dispatchers.IO) {
            // Busca todas as notas do banco de dados
            val todasNotas = notasDAOImpl.buscarTodas()
            withContext(Dispatchers.Main) {
                adapter.clear()
                adapter.addAll(todasNotas)
            }
        }

    }



    inner class NotaAdapter(context: Context, resource: Int, notas: List<Notas>) :
        ArrayAdapter<Notas>(context, resource, notas) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val itemView = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_nota, parent, false)

            val currentNota = getItem(position)
            val tvDescricao = itemView.findViewById<TextView>(R.id.tvDescricao)
            val tvData = itemView.findViewById<TextView>(R.id.tvData)
            val btnExcluir = itemView.findViewById<Button>(R.id.btnExcluir)

            // Define a descrição e a data da nota
            tvDescricao.text = currentNota?.descricao
            tvData.text = formatarData(currentNota?.data?.toLongOrNull() ?: 0)

            // Define o clique do botão de exclusão
            btnExcluir.setOnClickListener {
                Log.d("AT_Notas", "Botão Excluir clicado para a nota: ${currentNota?.descricao}")
                currentNota?.let {
                    // Execute a exclusão em uma coroutine
                    GlobalScope.launch(Dispatchers.IO) {
                        notasDAOImpl.remover(it)
                        // Atualiza a interface do usuário na thread principal
                        withContext(Dispatchers.Main) {
                            remove(it) // Remove a nota da lista
                            notifyDataSetChanged() // Notifica o adaptador sobre a alteração
                        }
                    }
                }
            }

            // Adiciona um OnClickListener para expandir/recolher a descrição
            itemView.setOnClickListener {
                toggleDescricao(tvDescricao)
            }

            return itemView
        }

        private fun toggleDescricao(tvDescricao: TextView) {
            if (tvDescricao.maxLines == 1) {
                // Se o texto estiver truncado, expande
                tvDescricao.maxLines = Int.MAX_VALUE
            } else {
                // Se estiver expandido, recolhe
                tvDescricao.maxLines = 1
            }
        }

        private fun formatarData(timestamp: Long): String {
            val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            return formato.format(timestamp)
        }

        private fun formatarHora(timestamp: Long): String {
            val formato = SimpleDateFormat(if (Locale.getDefault().language == "en") "hh:mm a" else "HH:mm", Locale.getDefault())
            return formato.format(timestamp)
        }
    }



}
