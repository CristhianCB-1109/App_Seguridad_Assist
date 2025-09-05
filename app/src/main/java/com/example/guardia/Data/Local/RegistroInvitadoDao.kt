package com.example.guardia.Data.Local
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.guardia.Data.Local.entities.RegistroInvitado

@Dao
interface RegistroInvitadoDao {
    @Insert
    suspend fun insertarInvitado(invitado: RegistroInvitado)

    @Query("SELECT * FROM registro_Invitado ORDER BY id DESC")
    suspend fun obtenerTodos(): List<RegistroInvitado>
}