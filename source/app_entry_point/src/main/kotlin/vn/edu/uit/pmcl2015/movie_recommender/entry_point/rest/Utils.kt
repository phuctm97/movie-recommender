package vn.edu.uit.pmcl2015.movie_recommender.entry_point.rest

import org.apache.commons.lang3.exception.ExceptionUtils

fun exceptionStackTrace(ex: Exception): String = ExceptionUtils.getStackTrace(ex)