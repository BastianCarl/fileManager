# SSE Upload Progress Implementation - Ghid Complet

## 📋 Ce a fost implementat

Am adăugat funcționalitatea **Server-Sent Events (SSE)** pentru notificări în timp real ale progresului upload-ului fișierelor.

## 📁 Fișiere Create/Modificate

### 1. **UploadProgressService.java** (NEW)
- **Locație**: `src/main/java/com/example/demo/service/UploadProgressService.java`
- **Responsabilitate**: Gestionează conexiunile SSE și notificările progresului
- **Metodele principale**:
  - `registerEmitter()` - înregistrează un nou client SSE
  - `removeEmitter()` - elimină un client la disconnect
  - `notifyProgress()` - trimite update cu stadiul curent
  - `notifyCompletion()` - notifică finalizarea
  - `notifyError()` - notifică erorile

### 2. **FileController.java** (MODIFICAT)
- **Import nou**: `org.springframework.web.servlet.mvc.method.annotation.SseEmitter`
- **Dependency nou**: `UploadProgressService uploadProgressService`
- **Endpoint nou**: `GET /files/{id}/progress` - punct de conectare SSE

**Modificări**:
```java
// Endpoint pentru SSE
@GetMapping("/{id}/progress")
public SseEmitter watchUploadProgress(@PathVariable String id) {
    SseEmitter emitter = new SseEmitter(60000L); // 60 secunde timeout
    uploadProgressService.registerEmitter(id, emitter);
    emitter.onCompletion(() -> uploadProgressService.removeEmitter(id));
    emitter.onTimeout(() -> uploadProgressService.removeEmitter(id));
    return emitter;
}

// Metoda upload - acum pasează serviciul
fileServiceOrchestrator.upload(tempFile, authToken, uuid, uploadProgressService);
```

### 3. **FileServiceOrchestrator.java** (MODIFICAT)
- **Import nou**: `com.example.demo.service.UploadProgressService`
- **Metoda nouă**: `upload(File, String, UUID, UploadProgressService)` - overload cu notificări

**Modificări**:
```java
@Async
public void upload(File file, String authToken, UUID uuid, UploadProgressService uploadProgressService) {
    try {
        // ... logica de upload ...
        for (Step currentStep : steps) {
            fileProcessingStep = currentStep.process(resource, fileProcessingStep, uuid);
            // Notifică progresul cu fiecare pas
            uploadProgressService.notifyProgress(uuid.toString(), fileProcessingStep);
        }
        // Notifică completarea
        uploadProgressService.notifyCompletion(uuid.toString(), fileProcessingStep);
    } catch (Exception e) {
        uploadProgressService.notifyError(uuid.toString(), e.getMessage());
    }
}
```

### 4. **upload.html** (NEW)
- **Locație**: `src/main/resources/static/upload.html`
- **Scop**: Pagină HTML pentru testarea upload-ului cu SSE
- **Functionalități**:
  - Upload fișier cu token autentificare
  - Afișare progres în timp real
  - Afișare stadii: METADATA → FILE_SERVICE → DISK → DONE
  - Notificări de eroare
  - UI responsive și modern

## 🚀 Cum se folosește

### Backend - Flux de lucru:

1. **Clientul trimite cererea POST** pe `/files`
2. **Server returnează imediat** UUID-ul fișierului cu status 202 Accepted
3. **Upload se procesează asincron** (@Async)
4. **Cu fiecare pas de procesare**, se notifică SSE emitter-ul:
   ```
   METADATA_STARTED → METADATA_DONE 
   → FILE_SERVICE_STARTED → FILE_SERVICE_DONE 
   → DISK_STARTED → DISK_DONE → DONE
   ```
5. **Clientul se poate conecta** la `/files/{uuid}/progress` pentru a primi notificări

### Frontend - Cum se conectează:

```javascript
// 1. Upload fișierul
const response = await fetch('/files', {
    method: 'POST',
    headers: { 'Authentication': authToken },
    body: formData
});
const fileId = await response.text();

// 2. Conectează-te la SSE
const eventSource = new EventSource(`/files/${fileId}/progress`);

// 3. Ascultă pentru update-uri
eventSource.addEventListener('progress', (event) => {
    const data = JSON.parse(event.data);
    console.log(`Stare: ${data.step} (${data.order})`);
});

// 4. Starea de finalizare
eventSource.addEventListener('complete', (event) => {
    console.log('Upload completat!');
    eventSource.close();
});

// 5. Gestionează erorile
eventSource.addEventListener('error', (event) => {
    console.error('Upload error:', event.data);
    eventSource.close();
});
```

## 🧪 Testare

### Opțiunea 1: Folosind pagina HTML
1. Navighează la: `http://localhost:8080/upload.html`
2. Completează:
   - **Authentication Token**: token-ul JWT/Bearer
   - **Select File**: alege un fișier
3. Apasă **Upload File**
4. Vizionează progresul în timp real

### Opțiunea 2: Folosind cURL

```bash
# 1. Upload fișier
curl -X POST \
  -H "Authentication: your-auth-token" \
  -F "file=@/path/to/file.zip" \
  http://localhost:8080/files

# Răspuns: UUID (de ex: 550e8400-e29b-41d4-a716-446655440000)

# 2. Conectează-te la SSE în alt terminal
curl -N http://localhost:8080/files/550e8400-e29b-41d4-a716-446655440000/progress
```

### Opțiunea 3: Folosind JavaScript în consolă

```javascript
// Upload
const file = document.getElementById('fileInput').files[0];
const formData = new FormData();
formData.append('file', file);

const response = await fetch('/files', {
    method: 'POST',
    headers: { 'Authentication': 'your-token' },
    body: formData
});

const fileId = await response.text();
console.log('File ID:', fileId);

// Conectare SSE
const sse = new EventSource(`/files/${fileId}/progress`);
sse.addEventListener('progress', e => console.log(JSON.parse(e.data)));
sse.addEventListener('complete', e => console.log('Done!'));
sse.addEventListener('error', e => console.error(JSON.parse(e.data)));
```

## 📊 Structura mesajelor SSE

### Mesaj Progress
```json
{
  "step": "FILE_SERVICE_DONE",
  "order": 5
}
```

### Mesaj Complete
```json
{
  "status": "success"
}
```

### Mesaj Error
```json
{
  "error": "Descrierea erorii..."
}
```

## ⚙️ Configurare

### Timeout-ul conexiunii SSE
Implicit: **60 secunde** (puteți modifica în `FileController`):
```java
SseEmitter emitter = new SseEmitter(120000L); // 2 minute
```

### Adresa endpoint-ului
- Upload: `POST /files`
- Progress: `GET /files/{id}/progress`
- Check Status: `GET /files/{id}` (metoda veche, cu polling)

## 🔒 Siguranta

- Conexiunea SSE necesită **header Authentication** pentru a se conecta inițial
- Emitter-ul este înregistrat cu UUID-ul fișierului
- Se recomandă adăugarea de validări de permisiuni pentru a asigura că doar proprietarul fișierului poate urmări progresul

## ⚠️ Considerații

1. **Timeout**: Dacă clientul nu se conectează la SSE în timp de 60 secunde, conexiunea expiră
2. **Disconnect**: Dacă clientul se deconectează, emitter-ul se curăță automat
3. **Memorie**: Emitter-urile inactive sunt eliminate, dar monitorizați dacă aveți mulți upload-uri paralele
4. **Browser Compatibility**: SSE este suportat pe toate browserele moderne (IE nu)

## 🐛 Troubleshooting

### "Connection refused" la SSE
- Asigură-te că ai așteptat să fie trimis UUID-ul de la POST
- Verifica dacă UUID-ul este corect

### "No events" / SSE nu trimite nimic
- Verifica dacă metodele `Step` sunt apelate corect
- Asigură-te că `notifyProgress()` este apelat pentru fiecare pas
- Verifica logs-ul aplicației

### Clientul se deconectează
- SSE-ul poate avea timeout la 60 secunde - alungește-l dacă trebuie
- Verifica network tab în DevTools pentru erori

## 📝 Schimbări viitoare (Opționale)

1. **Heartbeat**: Adaugă keep-alive messages pentru a evita timeout-uri
2. **Persistență**: Salveaza progresul în Redis pentru recuperare în caz de disconnect
3. **Queuing**: Implementează coadă de upload-uri cu prioritate
4. **WebSocket**: Migrezează la WebSocket pentru comunicație bidirecțională

