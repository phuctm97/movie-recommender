package vn.edu.uit.pmcl2015.movie_recommender.core.data_provider

import java.io.Closeable

interface UnitOfWork : Closeable {
  fun <T> flush(body: () -> T): T

  fun fetch(entity: Any)

  fun commit();

  fun rollback();
}

interface UnitOfWorkProvider {
  fun get(): UnitOfWork
}