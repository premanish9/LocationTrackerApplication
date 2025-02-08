package com.example.locationtrackerapplication.database


import androidx.room.*

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArticles(articles: List<ArticleEntity>)

    @Query("SELECT * FROM articles")
    suspend fun getAllArticles(): List<ArticleEntity>

    @Query("SELECT COUNT(*) FROM articles WHERE url = :articleUrl")
    suspend fun isArticleExists(articleUrl: String): Int

    @Transaction
    suspend fun insertIfNotExists(article: ArticleEntity) {
        if (isArticleExists(article.url) == 0) {
            insertArticles(listOf(article))
        }
    }
}

