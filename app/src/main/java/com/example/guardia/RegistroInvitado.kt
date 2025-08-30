package com.example.guardia


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registro_Invitado")
data class RegistroInvitado(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val dni: String,
    val numero: String
)
