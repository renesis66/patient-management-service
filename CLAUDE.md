# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Patient Management Service built with Micronaut and Kotlin, using DynamoDB for data storage. Focused on patient identity, demographics, and medical record number management.

## Technology Stack

- **Framework**: Micronaut 4.2.1
- **Language**: Kotlin 1.9.22
- **Database**: AWS DynamoDB (single-table design)
- **Build Tool**: Gradle with Kotlin DSL
- **Authentication**: JWT with Micronaut Security

## Common Commands

```bash
# Build the application
./gradlew build

# Run the application
./gradlew run

# Run tests
./gradlew test

# Run application with specific profile
./gradlew run --args="-Dmicronaut.environments=prod"

# Build shadow JAR
./gradlew shadowJar

# Clean build
./gradlew clean
```

## Starting Services

### DynamoDB Local
```bash
# Start DynamoDB Local (run from DynamoDB Local directory)
java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb -port 8000
```

### Patient Management Service
```bash
# Start with in-memory repository (for testing/development)
java -Dmicronaut.environments=test -jar build/libs/patient-management-service-0.1-all.jar

# Start with DynamoDB (requires DynamoDB Local to be running)
java -Dmicronaut.environments=dynamodb -jar build/libs/patient-management-service-0.1-all.jar

# Run in background
java -Dmicronaut.environments=test -jar build/libs/patient-management-service-0.1-all.jar > service.log 2>&1 &
```

Service will be available at: **http://localhost:8080**

## DynamoDB Table Design

Single table: `patients`

**Access Patterns:**
- Get patient by ID: PK = `PATIENT#{id}`, SK = `METADATA`
- Find patient by MRN: GSI1PK = `MRN#{mrn}`, GSI1SK = `PATIENT`

**Indexes:**
- Primary: PK, SK
- GSI1: GSI1PK, GSI1SK

## API Endpoints

Patient Identity & Demographics:
- `GET /patients` - List all patients
- `GET /patients/{id}` - Get patient by ID
- `POST /patients` - Create new patient
- `PUT /patients/{id}` - Update patient
- `DELETE /patients/{id}` - Delete patient

## Domain Focus

- Patient registration and demographics
- Medical record number (MRN) management
- Patient identity verification
- HIPAA compliant data handling

## Development Notes

- All endpoints require authentication (JWT)
- Patient data uses single-table DynamoDB design
- Medical Record Numbers must be unique (enforced via GSI1)
- Always update `updatedAt` timestamp on modifications
- Use proper HTTP status codes for medical data operations