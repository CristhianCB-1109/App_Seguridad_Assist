package com.example.guardia
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RegistroInvitadoDao {
    @Insert
    suspend fun insertarInvitado(invitado: RegistroInvitado)

    @Query("SELECT * FROM registro_Invitado ORDER BY id DESC")
    suspend fun obtenerTodos(): List<RegistroInvitado>
}