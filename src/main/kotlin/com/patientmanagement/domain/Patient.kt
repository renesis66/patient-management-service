package com.patientmanagement.domain

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey
import java.time.Instant

@DynamoDbBean
data class Patient(
    @get:DynamoDbPartitionKey
    var pk: String = "",
    
    @get:DynamoDbSortKey
    var sk: String = "METADATA",
    
    @get:DynamoDbSecondaryPartitionKey(indexNames = ["GSI1"])
    var gsi1pk: String = "",
    
    @get:DynamoDbSecondarySortKey(indexNames = ["GSI1"])
    var gsi1sk: String = "PATIENT",
    
    var id: String = "",
    var name: String = "",
    var dateOfBirth: String = "",
    var medicalRecordNumber: String = "",
    var status: String = "ACTIVE",
    var createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now()
) {
    companion object {
        fun createPK(id: String): String = "PATIENT#$id"
        fun createGSI1PK(mrn: String): String = "MRN#$mrn"
        const val METADATA_SK = "METADATA"
        const val PATIENT_GSI1SK = "PATIENT"
    }
}