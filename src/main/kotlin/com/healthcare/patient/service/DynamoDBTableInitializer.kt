package com.healthcare.patient.service

import com.healthcare.patient.domain.Patient
import io.micronaut.context.annotation.Requires
import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.runtime.server.event.ServerStartupEvent
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.EnhancedGlobalSecondaryIndex
import software.amazon.awssdk.services.dynamodb.model.BillingMode
import software.amazon.awssdk.services.dynamodb.model.Projection
import software.amazon.awssdk.services.dynamodb.model.ProjectionType
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException

@Singleton
@Requires(env = ["dynamodb"])
class DynamoDBTableInitializer(
    private val dynamoDbEnhancedClient: DynamoDbEnhancedClient
) : ApplicationEventListener<ServerStartupEvent> {

    private val logger = LoggerFactory.getLogger(DynamoDBTableInitializer::class.java)

    override fun onApplicationEvent(event: ServerStartupEvent) {
        createPatientTableIfNotExists()
    }

    private fun createPatientTableIfNotExists() {
        val tableName = "patients"
        val table: DynamoDbTable<Patient> = dynamoDbEnhancedClient.table(tableName, TableSchema.fromBean(Patient::class.java))

        try {
            table.describeTable()
            logger.info("Table '$tableName' already exists")
        } catch (e: ResourceNotFoundException) {
            logger.info("Creating table '$tableName'...")
            
            val gsi1 = EnhancedGlobalSecondaryIndex.builder()
                .indexName("GSI1")
                .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                .build()

            val createTableRequest = CreateTableEnhancedRequest.builder()
                .globalSecondaryIndices(gsi1)
                .build()

            table.createTable(createTableRequest)
            
            // Wait for table to be active
            table.describeTable().table().tableStatus()
            logger.info("Table '$tableName' created successfully")
        }
    }
}