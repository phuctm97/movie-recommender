package vn.edu.uit.pmcl2015.movie_recommender.entry_point.web

import vn.edu.uit.pmcl2015.movie_recommender.entry_point.EntryPointBootstrap

/****************************************************************************************************/
/* Bootstrap */

abstract class WebEntryPointBootstrap : EntryPointBootstrap {
  override fun run(vararg args: String?) {
  }
}

/****************************************************************************************************/
/* Config */

data class WebEntryPointConfig(val appPort: Int,
                               val appDebug: Boolean,
                               val appExitSecretKey: String)