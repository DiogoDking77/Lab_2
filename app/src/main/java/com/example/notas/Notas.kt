package com.example.notas

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
class Notas(
    @field:PrimaryKey(autoGenerate = true) val id: Int,
    val descricao: String,
    val data: String
)


