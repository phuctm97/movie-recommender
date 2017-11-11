package vn.edu.uit.pmcl2015.movie_recommender.entry_point

interface EntryPointBootstrap {
  fun run(vararg args: String?)

  fun exit(exitCode: Int)
}