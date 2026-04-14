# 📄 File API Documentation

## 🚀 Upload File

### 🔹 Endpoint

```
POST /files
```

---

## 🔐 Authentication

This endpoint requires JWT authentication.

**Header:**

```
Authentication: Bearer <JWT_TOKEN>
```

* The token must be prefixed with `Bearer `
* The token is validated and used to identify the user

---

## 📥 Request

### Content-Type

```
multipart/form-data
```

### Parameters

| Name | Type          | Required | Description         |
| ---- | ------------- | -------- | ------------------- |
| file | MultipartFile | Yes      | File to be uploaded |

---

## ⚙️ Behavior

The upload process is **partially synchronous and partially asynchronous**.

---

### 🔹 Synchronous Processing

The following step is executed **before the response is returned**:

#### 1. Metadata Processing

* Status transition:

  ```
  METADATA_STARTED → METADATA_DONE
  ```
* File metadata is extracted and saved
* This step ensures the system has the necessary information about the file before continuing

---

### 🔹 Asynchronous Processing Pipeline

After the response is returned, the remaining processing continues asynchronously:

#### 2. File Upload Service

* Status transition:

  ```
  FILE_SERVICE_STARTED → FILE_SERVICE_DONE
  ```
* The file is uploaded to the storage system

---

### 📊 Audit & Tracking

* Each step is audited
* Progress can be tracked in real time via SSE

---

## 📡 Progress Monitoring (SSE)

After uploading the file, the client should subscribe to progress updates:

```
GET /files/{id}/progress/stream
```

This endpoint provides:

* Real-time progress updates
* Current processing status

---

## 📤 Response

### Status

```
202 Accepted
```

---

### Headers

| Header   | Description                       |
| -------- | --------------------------------- |
| Location | URL of the uploaded file resource |

---

### Body

Returns the file identifier:

```
<UUID>
```

**Example:**

```
a3f1c2d4-5678-90ab-cdef-1234567890ab
```

---

## 🔗 Location Header

Indicates where the file resource will be available:

```
/files/{id}
```

---

## 🔄 Flow Overview

1. Client uploads file
2. Server executes metadata processing (synchronous)
3. Server responds with:

    * `202 Accepted`
    * file `UUID`
4. Remaining processing continues asynchronously
5. Client subscribes to:

   ```
   /files/{id}/progress/stream
   ```
6. Server streams progress updates

---

## 💡 Notes

* There is **no file size limit**
* Upload is **not completed at response time**
* Clients **must use SSE** to track progress


## 📦 Download Files as Archive

### 🔹 Endpoint

```
GET /files?type={type}&version={version}
```

---

## 🔐 Authentication

This endpoint requires JWT authentication.

**Header:**

```
Authentication: Bearer <JWT_TOKEN>
```

---

## 📥 Request

### Query Parameters

| Name    | Type   | Required | Description                     |
| ------- | ------ | -------- | ------------------------------- |
| type    | String | Yes      | Archive format (e.g. `zip`)     |
| version | String | Yes      | File version selection strategy |

---

## 🔢 Version Parameter

The `version` parameter controls which file versions are included in the archive.

### Allowed values:

| Value  | Description                                       |
| ------ | ------------------------------------------------- |
| all    | Includes **all versions** of each file            |
| latest | Includes **only the latest version** of each file |

---

## ⚙️ Behavior

* The server retrieves files based on the selected `version`
* Files are grouped and compressed into an archive
* The archive format is defined by the `type` parameter (e.g. `zip`)

---

## 📤 Response

### Status

```
200 OK
```

---

### Headers

| Header              | Description                           |
| ------------------- | ------------------------------------- |
| Content-Type        | application/zip                       |
| Content-Disposition | attachment; filename="<archive-name>" |

---

### Body

Binary content of the generated archive.

---

## 📦 Archive Content

The contents of the archive depend on the `version` parameter:

* **latest**

    * Contains only the most recent version of each file

* **all**

    * Contains all available versions of each file

---

## 🔄 Flow Overview

1. Client sends request with `type` and `version`
2. Server:

    * retrieves matching files
    * builds archive
3. Server returns archive as downloadable file

---

## 💡 Notes

* The archive is generated dynamically at request time
* The response is returned as a binary stream

## 📡 File Upload Progress Stream (SSE)

### 🔹 Endpoint

```
GET /files/{id}/progress/stream
```

---

## 📥 Path Parameters

| Name | Type          | Required | Description                     |
| ---- | ------------- | -------- | ------------------------------- |
| id   | String (UUID) | Yes      | Identifier of the uploaded file |

---

## 📡 Description

This endpoint provides **real-time updates** about the file upload and processing status using **Server-Sent Events (SSE)**.

Clients can subscribe to this endpoint to track upload progress.

---

## 📤 Response

### Content-Type

```
text/event-stream
```

---

## 📦 Event Payload

Each event contains a JSON object with the following structure:

```json
{
  "status": "IN_PROGRESS",
  "progress": 45,
  "message": "Processing metadata"
}
```

---

## 🔢 Fields Description

| Field    | Type    | Description                                    |
| -------- | ------- | ---------------------------------------------- |
| status   | String  | Current processing state                       |
| progress | Integer | Progress percentage (0–100)                    |
| message  | String  | Human-readable description of the current step |

---

## 🚦 Status Values

| Status      | Description            |
| ----------- | ---------------------- |
| STARTED     | Processing has begun   |
| IN_PROGRESS | Processing is ongoing  |
| DONE        | Processing is complete |

---

## 🔄 Event Flow

The server sends updates:

* when processing starts
* during processing (progress updates)
* when each step is completed
* when the entire process is finished

---

## ✅ Completion Event

The final event has the following structure:

```json
{
  "status": "DONE",
  "progress": 100,
  "message": "Completed"
}
```

After this event:

* the stream is closed by the server


## 🔄 Flow Overview

1. Client uploads file
2. Receives file `id`
3. Subscribes to:

   ```
   /files/{id}/progress/stream
   ```
4. Receives real-time updates
5. Stream closes when processing reaches `DONE`

---

## 💡 Notes

* Progress is reported as a percentage (0–100)
* The final event always contains:

    * `status = DONE`
    * `progress = 100`
* This endpoint is essential for tracking asynchronous upload processing
