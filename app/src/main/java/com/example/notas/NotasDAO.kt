package com.example.notas

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NotasDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun inserir(nota: Notas): Long

    @Query("UPDATE notas SET descricao = :descricao, data = :data WHERE id = :id")
    fun atualizar(descricao: String, data: String, id: Int): Int

    @Delete
    fun remover(nota: Notas): Int

    @Query("SELECT * FROM notas")
    fun buscarTodas(): List<Notas>

    @Query("SELECT * FROM notas WHERE id = :id")
    fun buscarPorId(id: Int): Notas?
}


