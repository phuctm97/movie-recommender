package vn.edu.uit.pmcl2015.movie_recommender.entry_point.rest

import com.google.gson.Gson
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import vn.edu.uit.pmcl2015.movie_recommender.core.currentTimestamp
import vn.edu.uit.pmcl2015.movie_recommender.core.entity.CoreException
import vn.edu.uit.pmcl2015.movie_recommender.entry_point.EntryPointBootstrap

/****************************************************************************************************/
/* Bootstrap */

abstract class RestEntryPointBootstrap : EntryPointBootstrap {
  override fun run(vararg args: String?) {
  }
}

/****************************************************************************************************/
/* Config */

data class RestEntryPointConfig(val appPort: Int,
                                val appDebug: Boolean,
                                val appExitSecretKey: String)

/****************************************************************************************************/
/* Exception Handler */

data class ErrorDto(val errorCode: String, val errorName: String,
                    val status: Int,
                    val userMessage: String,
                    val developerMessage: String = userMessage,
                    val moreInformation: String = "")

@ControllerAdvice
class RestResponseExceptionHandler {
  companion object {
    val LOGGER = LoggerFactory.getLogger(RestResponseExceptionHandler::class.java)!!
  }

  @ExceptionHandler(RuntimeException::class)
  fun handleException(ex: RuntimeException, request: WebRequest): ResponseEntity<ErrorDto> {
    if (ex is CoreException) return handleCoreException(ex)
    return handleUnexpectedException(ex, request)
  }

  private fun handleCoreException(ex: CoreException): ResponseEntity<ErrorDto> {
    val errorDto = ErrorDto(ex.errorCode,
                            ex.javaClass.simpleName,
                            coreExceptionStatusCode(),
                            ex.userMessage,
                            ex.developerMessage,
                            ex.moreInformation)
    return ResponseEntity(errorDto, HttpStatus.valueOf(errorDto.status))
  }

  private fun handleUnexpectedException(ex: RuntimeException, request: WebRequest): ResponseEntity<ErrorDto> {
    LOGGER.error(exceptionStackTrace(ex))

    val moreInformationMap = mutableMapOf<String, Any>()
    moreInformationMap["time"] = currentTimestamp()
    moreInformationMap["request"] = request.contextPath

    val moreInformation = Gson().toJson(moreInformationMap)

    val errorDto = ErrorDto("UE999999",
                            "Unexpected Exception",
                            unexpectedExceptionStatusCode(),
                            unexpectedExceptionMessage(),
                            exceptionStackTrace(ex),
                            moreInformation)
    return ResponseEntity(errorDto, HttpStatus.valueOf(errorDto.status))
  }
}