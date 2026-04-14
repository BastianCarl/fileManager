## 🖼️ Image Variants

### 🔹 Endpoint

```id="g0m3a1"
GET /images/{imageId}?type={type}
```

---

## 📥 Path Parameters

| Name    | Type | Required | Description             |
| ------- | ---- | -------- | ----------------------- |
| imageId | Long | Yes      | Identifier of the image |

---

## 📥 Query Parameters

| Name | Type   | Required | Description        |
| ---- | ------ | -------- | ------------------ |
| type | String | Yes      | Image variant type |

---

## 🔢 Image Types

The `type` parameter defines the size of the returned image.

| Type      | Width  | Height | Description              |
| --------- | ------ | ------ | ------------------------ |
| THUMBNAIL | 150px  | 150px  | Small preview image      |
| MAIN_LIST | 600px  | 600px  | Medium size for listings |
| FULL_HD   | 1920px | 1080px | High resolution image    |

---

## ⚙️ Behavior

* The original image is stored in **S3**
* When requested:

    1. The system checks if the processed image exists in cache
    2. If found → returns cached version
    3. If not:

        * retrieves original image from S3
        * resizes it according to `type`
        * stores the result in cache
        * returns the processed image

---

## ⚡ Caching Strategy

* Processed images are cached in **Redis**
* Cache key is based on:

  ```
  imageId + type
  ```
* Images are generated **on-demand** (lazy processing)
* Subsequent requests are served directly from cache

---

## 📤 Response

### Content-Type

```
image/jpeg
```

---

### Body

Binary content of the processed image.

---

## 🔄 Flow Overview

1. Client requests:

   ```
   /images/{imageId}?type=THUMBNAIL
   ```
2. Server checks Redis cache
3. If not cached:

    * fetches original image from S3
    * resizes image
    * stores result in cache
4. Returns processed image

---

## 💡 Notes

* Image processing is performed only once per `{imageId, type}`
* Cached images significantly improve performance for repeated requests
* This endpoint is optimized for fast image delivery in UI scenarios (e.g. lists, previews)
