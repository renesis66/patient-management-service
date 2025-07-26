package com.patientmanagement.service

import com.patientmanagement.domain.Patient
import com.patientmanagement.repository.InMemoryPatientRepository
import jakarta.inject.Singleton
import java.time.Instant
import java.util.*

@Singleton
class PatientService(
    private val patientRepository: InMemoryPatientRepository
) {
    
    fun createPatient(patient: Patient): Patient {
        val patientId = UUID.randomUUID().toString()
        val patientWithKeys = patient.copy(
            id = patientId,
            pk = Patient.createPK(patientId),
            sk = Patient.METADATA_SK,
            gsi1pk = Patient.createGSI1PK(patient.medicalRecordNumber),
            gsi1sk = Patient.PATIENT_GSI1SK,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        return patientRepository.save(patientWithKeys)
    }

    fun getPatient(patientId: String): Patient? {
        return patientRepository.findById(patientId).orElse(null)
    }

    fun getAllPatients(): List<Patient> {
        return patientRepository.findAll()
    }

    fun updatePatient(patientId: String, updatedPatient: Patient): Patient? {
        return patientRepository.findById(patientId)
            .map { existingPatient ->
                val patientToUpdate = updatedPatient.copy(
                    id = existingPatient.id,
                    pk = existingPatient.pk,
                    sk = existingPatient.sk,
                    gsi1pk = Patient.createGSI1PK(updatedPatient.medicalRecordNumber),
                    gsi1sk = existingPatient.gsi1sk,
                    createdAt = existingPatient.createdAt,
                    updatedAt = Instant.now()
                )
                patientRepository.save(patientToUpdate)
            }
            .orElse(null)
    }

    fun deletePatient(patientId: String): Boolean {
        return patientRepository.deleteById(patientId)
    }

    fun findByMedicalRecordNumber(mrn: String): Patient? {
        return patientRepository.findByMedicalRecordNumber(mrn).orElse(null)
    }
}