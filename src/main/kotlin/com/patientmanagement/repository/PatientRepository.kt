package com.patientmanagement.repository

import com.patientmanagement.domain.Patient
import jakarta.inject.Singleton
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import java.util.*

@Singleton
class PatientRepository(
    private val dynamoDbEnhancedClient: DynamoDbEnhancedClient
) {
    private val table: DynamoDbTable<Patient> = dynamoDbEnhancedClient
        .table("patients", TableSchema.fromBean(Patient::class.java))
    
    private val gsi1: DynamoDbIndex<Patient> = table.index("GSI1")

    fun save(patient: Patient): Patient {
        table.putItem(patient)
        return patient
    }

    fun findById(patientId: String): Optional<Patient> {
        val key = Key.builder()
            .partitionValue(Patient.createPK(patientId))
            .sortValue(Patient.METADATA_SK)
            .build()
        
        return Optional.ofNullable(table.getItem(key))
    }

    fun findByMedicalRecordNumber(mrn: String): Optional<Patient> {
        val queryConditional = QueryConditional.keyEqualTo(
            Key.builder()
                .partitionValue(Patient.createGSI1PK(mrn))
                .sortValue(Patient.PATIENT_GSI1SK)
                .build()
        )
        
        return gsi1.query(queryConditional)
            .items()
            .firstOrNull()
            ?.let { Optional.of(it) }
            ?: Optional.empty()
    }

    fun findAll(): List<Patient> {
        return table.scan().items().toList()
    }

    fun deleteById(patientId: String): Boolean {
        val key = Key.builder()
            .partitionValue(Patient.createPK(patientId))
            .sortValue(Patient.METADATA_SK)
            .build()
        
        val deletedItem = table.deleteItem(key)
        return deletedItem != null
    }
}