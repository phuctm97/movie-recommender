package vn.edu.uit.pmcl2015.movie_recommender.core.entity

@Suppress("MemberVisibilityCanPrivate")
open class CoreException(val errorCode: String,
                         val userMessage: String,
                         val developerMessage: String = userMessage,
                         val moreInformation: String = "")
  : RuntimeException()

class InvalidArgumentException(userMessage: String = "Input argument is invalid", developerMessage: String = userMessage, moreInformation: String = "")
  : CoreException("CE000001", userMessage, developerMessage, moreInformation)

class NotSupportedOperationException(userMessage: String = "Unsupported operation", developerMessage: String = userMessage, moreInformation: String = "")
  : CoreException("CE000002", userMessage, developerMessage, moreInformation)
