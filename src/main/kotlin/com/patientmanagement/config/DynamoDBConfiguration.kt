package com.patientmanagement.config

import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Primary
import io.micronaut.context.annotation.Replaces
import io.micronaut.context.annotation.Requires
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.net.URI

@Factory
@Requires(env = ["dynamodb"])
class DynamoDBConfiguration {

    @Bean
    @Singleton
    @Primary
    @Replaces(DynamoDbClient::class)
    fun dynamoDbClient(
        @Value("\${aws.region:us-east-1}") region: String,
        @Value("\${aws.dynamodb.endpoint:http://localhost:8000}") endpoint: String
    ): DynamoDbClient {
        val builder = DynamoDbClient.builder()
            .region(Region.of(region))

        // For local development, use dummy credentials and local endpoint
        if (endpoint.contains("localhost")) {
            builder.endpointOverride(URI.create(endpoint))
                .credentialsProvider(
                    StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("dummy", "dummy")
                    )
                )
        }

        return builder.build()
    }

    @Bean
    @Singleton
    @Primary
    fun dynamoDbEnhancedClient(dynamoDbClient: DynamoDbClient): DynamoDbEnhancedClient {
        return DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build()
    }
}