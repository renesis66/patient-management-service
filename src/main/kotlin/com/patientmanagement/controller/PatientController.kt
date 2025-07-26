package com.patientmanagement.controller

import com.patientmanagement.domain.Patient
import com.patientmanagement.service.PatientService
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule

@Controller("/patients")
@Secured(SecurityRule.IS_AUTHENTICATED)
class PatientController(
    private val patientService: PatientService
) {

    @Post
    fun createPatient(@Body patient: Patient): HttpResponse<Patient> {
        val createdPatient = patientService.createPatient(patient)
        return HttpResponse.status<Patient>(HttpStatus.CREATED).body(createdPatient)
    }

    @Get("/{id}")
    fun getPatient(@PathVariable id: String): HttpResponse<Patient> {
        val patient = patientService.getPatient(id)
        return if (patient != null) {
            HttpResponse.ok(patient)
        } else {
            HttpResponse.notFound()
        }
    }

    @Get
    fun getAllPatients(): HttpResponse<List<Patient>> {
        val patients = patientService.getAllPatients()
        return HttpResponse.ok(patients)
    }

    @Put("/{id}")
    fun updatePatient(
        @PathVariable id: String,
        @Body patient: Patient
    ): HttpResponse<Patient> {
        val updatedPatient = patientService.updatePatient(id, patient)
        return if (updatedPatient != null) {
            HttpResponse.ok(updatedPatient)
        } else {
            HttpResponse.notFound()
        }
    }

    @Delete("/{id}")
    fun deletePatient(@PathVariable id: String): HttpResponse<Void> {
        val deleted = patientService.deletePatient(id)
        return if (deleted) {
            HttpResponse.noContent()
        } else {
            HttpResponse.notFound()
        }
    }
}