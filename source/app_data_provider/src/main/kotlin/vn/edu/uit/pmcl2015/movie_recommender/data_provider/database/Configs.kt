package vn.edu.uit.pmcl2015.movie_recommender.data_provider.database

/***********************************************************************************************************/
/* Configs */

data class DatabaseDataProviderConfig(val host: String, val port: Int, val catalog: String, val schemaAction: String, val utf8: Boolean,
                                      val username: String, val password: String,
                                      val debug: Boolean) {
}