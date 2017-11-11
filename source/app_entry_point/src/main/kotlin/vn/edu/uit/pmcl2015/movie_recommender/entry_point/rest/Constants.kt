package vn.edu.uit.pmcl2015.movie_recommender.entry_point.rest

fun coreExceptionStatusCode(): Int = 400

fun unexpectedExceptionStatusCode(): Int = 500

fun unexpectedExceptionMessage(): String = "There is an unexpected error happen from server side!"
