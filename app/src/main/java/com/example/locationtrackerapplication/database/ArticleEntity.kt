package com.example.locationtrackerapplication.database


import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "articles", indices = [Index(value = ["url"], unique = true)])
data class ArticleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sourceName: String,
    val author: String?,
    val title: String,
    val description: String,
    val url: String,  // Unique constraint
    val urlToImage: String?,
    val publishedAt: String,
    val content: String?
)

