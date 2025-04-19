package com.ProyectoMAD.room

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "coordinates")
data class CoordinatesEntity(
    @PrimaryKey val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val name: String,
    val description: String,
    val new_column_name: String? = "" // Match the migration's default value
)

