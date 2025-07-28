package com.healthcare.patient.repository

import com.healthcare.patient.domain.Patient
import io.micronaut.context.annotation.Primary
import jakarta.inject.Singleton
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Singleton
@Primary
class InMemoryPatientRepository {
    private val patients = ConcurrentHashMap<String, Patient>()
    private val mrnIndex = ConcurrentHashMap<String, String>() // MRN -> Patient ID

    fun save(patient: Patient): Patient {
        patients[patient.id] = patient
        mrnIndex[patient.medicalRecordNumber] = patient.id
        return patient
    }

    fun findById(patientId: String): Optional<Patient> {
        return Optional.ofNullable(patients[patientId])
    }

    fun findByMedicalRecordNumber(mrn: String): Optional<Patient> {
        val patientId = mrnIndex[mrn]
        return if (patientId != null) {
            Optional.ofNullable(patients[patientId])
        } else {
            Optional.empty()
        }
    }

    fun findAll(): List<Patient> {
        return patients.values.toList()
    }

    fun deleteById(patientId: String): Boolean {
        val patient = patients.remove(patientId)
        return if (patient != null) {
            mrnIndex.remove(patient.medicalRecordNumber)
            true
        } else {
            false
        }
    }
}