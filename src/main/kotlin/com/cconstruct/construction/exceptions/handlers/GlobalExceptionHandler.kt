package com.cconstruct.construction.exceptions.handlers

import com.cconstruct.construction.exceptions.EvidenceAlreadyExistsException
import com.cconstruct.construction.exceptions.EvidenceNotFoundException
import com.cconstruct.construction.exceptions.ProgressNotFoundException
import com.cconstruct.construction.exceptions.SiteAlreadyExistsException
import com.cconstruct.construction.exceptions.SiteNotFoundException
import com.cconstruct.construction.exceptions.UserAlreadyExistsException
import com.cconstruct.construction.exceptions.UserNotFoundException
import com.cconstruct.construction.exceptions.WorkerAlreadyExistsException
import com.cconstruct.construction.exceptions.WorkerNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(ex: UserNotFoundException): ResponseEntity<Map<String, String>> =
        ResponseEntity(mapOf("error" to ex.message.orEmpty()), HttpStatus.NOT_FOUND)

    @ExceptionHandler(SiteNotFoundException::class)
    fun handleSiteNotFound(ex: SiteNotFoundException): ResponseEntity<Map<String, String>> =
        ResponseEntity(mapOf("error" to ex.message.orEmpty()), HttpStatus.NOT_FOUND)

    @ExceptionHandler(WorkerNotFoundException::class)
    fun handleWorkerNotFound(ex: WorkerNotFoundException): ResponseEntity<Map<String, String>> =
        ResponseEntity(mapOf("error" to ex.message.orEmpty()), HttpStatus.NOT_FOUND)

    @ExceptionHandler(ProgressNotFoundException::class)
    fun handleProgressNotFound(ex: ProgressNotFoundException): ResponseEntity<Map<String, String>> =
        ResponseEntity(mapOf("error" to ex.message.orEmpty()), HttpStatus.NOT_FOUND)

    @ExceptionHandler(EvidenceNotFoundException::class)
    fun handleEvidenceNotFound(ex: EvidenceNotFoundException): ResponseEntity<Map<String, String>> =
        ResponseEntity(mapOf("error" to ex.message.orEmpty()), HttpStatus.NOT_FOUND)

    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserExists(ex: UserAlreadyExistsException): ResponseEntity<Map<String, String>> =
        ResponseEntity(mapOf("error" to ex.message.orEmpty()), HttpStatus.CONFLICT)

    @ExceptionHandler(SiteAlreadyExistsException::class)
    fun handleSiteExists(ex: SiteAlreadyExistsException): ResponseEntity<Map<String, String>> =
        ResponseEntity(mapOf("error" to ex.message.orEmpty()), HttpStatus.CONFLICT)

    @ExceptionHandler(WorkerAlreadyExistsException::class)
    fun handleWorkerExists(ex: WorkerAlreadyExistsException): ResponseEntity<Map<String, String>> =
        ResponseEntity(mapOf("error" to ex.message.orEmpty()), HttpStatus.CONFLICT)

    @ExceptionHandler(EvidenceAlreadyExistsException::class)
    fun handleEvidenceExists(ex: EvidenceAlreadyExistsException): ResponseEntity<Map<String, String>> =
        ResponseEntity(mapOf("error" to ex.message.orEmpty()), HttpStatus.CONFLICT)

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<Map<String, String>> =
        ResponseEntity(mapOf("error" to ex.message.orEmpty()), HttpStatus.BAD_REQUEST)

}