package com.example.notas

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Notas::class], version = 1)
abstract class NotasDatabase : RoomDatabase() {
    abstract fun notasDao(): NotasDAO
}

class NotasDAOImpl(private val notasDAO: NotasDAO) {
    fun inserir(nota: Notas): Long {
        return notasDAO.inserir(nota)
    }

    fun atualizar(descricao: String, data: String, id: Int): Int {
        return notasDAO.atualizar(descricao, data, id)
    }

    fun remover(nota: Notas): Int {
        return notasDAO.remover(nota)
    }

    fun buscarTodas(): List<Notas> {
        return notasDAO.buscarTodas()
    }

    fun buscarPorId(id: Int): Notas? {
        return notasDAO.buscarPorId(id)
    }
}


