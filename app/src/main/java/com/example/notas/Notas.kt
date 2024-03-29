package com.example.notas

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
class Notas(
    @field:PrimaryKey(autoGenerate = true) val id: Int,
    val descricao: String,
    val data: String
){
    override fun toString(): String {
        return "Descrição: $descricao - Data: $data"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Notas

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}


