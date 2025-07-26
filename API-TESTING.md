# API Testing Guide for Patient Management Service

This document demonstrates how to test the Patient Management Service API endpoints using curl commands.

## Service Structure

The service provides the following endpoints:
- `GET /patients` - List all patients
- `GET /patients/{id}` - Get patient by ID  
- `POST /patients` - Create new patient
- `PUT /patients/{id}` - Update patient
- `DELETE /patients/{id}` - Delete patient

## DynamoDB Data Model

Patients are stored using single-table design:
```json
{
  "PK": "PATIENT#123e4567-e89b-12d3-a456-426614174000",
  "SK": "METADATA", 
  "GSI1PK": "MRN#MRN001234",
  "GSI1SK": "PATIENT",
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "John Doe",
  "dateOfBirth": "1980-05-15",
  "medicalRecordNumber": "MRN001234",
  "status": "ACTIVE",
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z"
}
```

## Testing Commands

Assuming the service is running on `http://localhost:8080`:

### 1. Create a Patient

```bash
curl -X POST http://localhost:8080/patients \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "dateOfBirth": "1980-05-15", 
    "medicalRecordNumber": "MRN001234",
    "status": "ACTIVE"
  }'
```

Expected Response:
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "pk": "PATIENT#123e4567-e89b-12d3-a456-426614174000",
  "sk": "METADATA",
  "gsi1pk": "MRN#MRN001234", 
  "gsi1sk": "PATIENT",
  "name": "John Doe",
  "dateOfBirth": "1980-05-15",
  "medicalRecordNumber": "MRN001234",
  "status": "ACTIVE",
  "createdAt": "2024-07-26T15:00:00Z",
  "updatedAt": "2024-07-26T15:00:00Z"
}
```

### 2. Get All Patients

```bash
curl -X GET http://localhost:8080/patients
```

Expected Response:
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "name": "John Doe",
    "dateOfBirth": "1980-05-15",
    "medicalRecordNumber": "MRN001234", 
    "status": "ACTIVE",
    "createdAt": "2024-07-26T15:00:00Z",
    "updatedAt": "2024-07-26T15:00:00Z"
  }
]
```

### 3. Get Patient by ID

```bash
curl -X GET http://localhost:8080/patients/123e4567-e89b-12d3-a456-426614174000
```

Expected Response:
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "John Doe", 
  "dateOfBirth": "1980-05-15",
  "medicalRecordNumber": "MRN001234",
  "status": "ACTIVE",
  "createdAt": "2024-07-26T15:00:00Z",
  "updatedAt": "2024-07-26T15:00:00Z"
}
```

### 4. Update Patient

```bash
curl -X PUT http://localhost:8080/patients/123e4567-e89b-12d3-a456-426614174000 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Smith",
    "dateOfBirth": "1980-05-15",
    "medicalRecordNumber": "MRN001234", 
    "status": "ACTIVE"
  }'
```

Expected Response:
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "John Smith",
  "dateOfBirth": "1980-05-15", 
  "medicalRecordNumber": "MRN001234",
  "status": "ACTIVE",
  "createdAt": "2024-07-26T15:00:00Z",
  "updatedAt": "2024-07-26T15:05:00Z"
}
```

### 5. Delete Patient

```bash
curl -X DELETE http://localhost:8080/patients/123e4567-e89b-12d3-a456-426614174000
```

Expected Response: `204 No Content`

### 6. Verify Deletion

```bash
curl -X GET http://localhost:8080/patients/123e4567-e89b-12d3-a456-426614174000
```

Expected Response: `404 Not Found`

## Error Scenarios

### Patient Not Found
```bash
curl -X GET http://localhost:8080/patients/nonexistent-id
```
Response: `404 Not Found`

### Invalid JSON
```bash
curl -X POST http://localhost:8080/patients \
  -H "Content-Type: application/json" \
  -d '{"invalid": json}'
```
Response: `400 Bad Request`

## Test Sequence

A complete test sequence would be:

1. **Create Patient** - Verify 201 status and returned patient data
2. **List Patients** - Verify patient appears in list  
3. **Get Patient by ID** - Verify specific patient can be retrieved
4. **Update Patient** - Verify patient data is modified
5. **Delete Patient** - Verify 204 status
6. **Verify Deletion** - Confirm patient no longer exists (404)

## Notes

- All endpoints require authentication in production (currently disabled for testing)
- The service uses DynamoDB for persistence with single-table design
- Medical Record Numbers (MRN) must be unique across patients
- Patient IDs are auto-generated UUIDs
- Timestamps are automatically managed by the service