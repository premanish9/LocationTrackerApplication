package com.example.locationtrackerapplication.database


class NewsRepository(private val articleDao: ArticleDao) {

    // Insert articles
    suspend fun insertArticles(articles: List<ArticleEntity>) {
        articleDao.insertArticles(articles)
    }

    suspend fun saveArticles(articles: List<ArticleEntity>, dao: ArticleDao) {
        //val articleEntities = articles.toEntityList()
        articles.forEach { dao.insertIfNotExists(it) }
    }

    // Fetch articles
    suspend fun getAllArticles(): List<ArticleEntity> {
        return articleDao.getAllArticles()
    }

    // Delete all articles

}
