# Board & BoardImage API 가이드

## 📋 Board API

### 1. 게시글 생성
**Endpoint:** `POST /api/boards`  
**인증:** 필요 (JWT)

**Request Body:**
```json
{
  "title": "게시글 제목",
  "content": "게시글 내용",
  "boardImage": {
    "imageId": 1
  }
}
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "게시글 작성 성공",
  "data": {
    "boardId": 1,
    "title": "게시글 제목",
    "content": "게시글 내용",
    "createAt": "2024-11-02T10:30:00",
    "updateAt": "2024-11-02T10:30:00",
    "user": { ... },
    "boardImage": { ... }
  }
}
```

---

### 2. 게시글 단건 조회
**Endpoint:** `GET /api/boards/{boardId}`  
**인증:** 불필요

**Response:**
```json
{
  "status": "SUCCESS",
  "data": {
    "boardId": 1,
    "title": "게시글 제목",
    "content": "게시글 내용",
    "createAt": "2024-11-02T10:30:00",
    "updateAt": "2024-11-02T10:30:00",
    "user": { ... },
    "boardImage": { ... }
  }
}
```

---

### 3. 게시글 전체 조회
**Endpoint:** `GET /api/boards`  
**인증:** 불필요

**Response:**
```json
{
  "status": "SUCCESS",
  "data": [
    {
      "boardId": 1,
      "title": "게시글 제목",
      "content": "게시글 내용",
      "createAt": "2024-11-02T10:30:00",
      "updateAt": "2024-11-02T10:30:00",
      "user": { ... },
      "boardImage": { ... }
    }
  ]
}
```

---

### 4. 게시글 수정
**Endpoint:** `PATCH /api/boards/{boardId}`  
**인증:** 필요 (JWT, 작성자만 가능)

**Request Body:** (수정할 필드만 전송 가능)
```json
{
  "title": "수정된 제목",
  "content": "수정된 내용",
  "boardImage": {
    "imageId": 2
  }
}
```

**Request Body 예시 2:** (제목만 수정)
```json
{
  "title": "제목만 수정"
}
```

**Request Body 예시 3:** (내용만 수정)
```json
{
  "content": "내용만 수정"
}
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "게시글 수정 성공",
  "data": {
    "boardId": 1,
    "title": "수정된 제목",
    "content": "수정된 내용",
    "createAt": "2024-11-02T10:30:00",
    "updateAt": "2024-11-02T11:45:00",
    "user": { ... },
    "boardImage": { ... }
  }
}
```

---

### 5. 게시글 삭제
**Endpoint:** `DELETE /api/boards/{boardId}`  
**인증:** 필요 (JWT, 작성자만 가능)

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "게시글 삭제 성공",
  "data": null
}
```

---

## 🖼️ BoardImage API

### 1. 이미지 업로드
**Endpoint:** `POST /api/board-images/upload`  
**인증:** 필요 (JWT)  
**Content-Type:** `multipart/form-data`

**Request:**
```
POST /api/board-images/upload
Content-Type: multipart/form-data

file: [이미지 파일]
```

**Response:**
```json
{
  "status": "SUCCESS",
  "data": {
    "imageId": 1,
    "fileName": "original-image.jpg",
    "filePath": "550e8400-e29b-41d4-a716-446655440000.jpg"
  }
}
```

---

### 2. 이미지 조회 (파일 다운로드)
**Endpoint:** `GET /api/board-images/{imageId}`  
**인증:** 불필요

**Response:** 이미지 파일 (Binary)

---

### 3. 이미지 정보 조회
**Endpoint:** `GET /api/board-images/{imageId}/info`  
**인증:** 불필요

**Response:**
```json
{
  "status": "SUCCESS",
  "data": {
    "imageId": 1,
    "fileName": "original-image.jpg",
    "filePath": "550e8400-e29b-41d4-a716-446655440000.jpg"
  }
}
```

---

### 4. 이미지 삭제
**Endpoint:** `DELETE /api/board-images/{imageId}`  
**인증:** 필요 (JWT)

**Response:**
```json
{
  "status": "SUCCESS",
  "data": null
}
```

---

## 🔄 일반적인 사용 흐름

### 게시글 작성 (이미지 포함)

1. **이미지 업로드**
```bash
curl -X POST http://localhost:8082/api/board-images/upload \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -F "file=@/path/to/image.jpg"
```

2. **게시글 작성 (업로드된 이미지 ID 사용)**
```bash
curl -X POST http://localhost:8082/api/boards \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "게시글 제목",
    "content": "게시글 내용",
    "boardImage": {
      "imageId": 1
    }
  }'
```

### 게시글 수정 (일부 필드만)

```bash
# 제목만 수정
curl -X PATCH http://localhost:8082/api/boards/1 \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "수정된 제목"
  }'

# 내용만 수정
curl -X PATCH http://localhost:8082/api/boards/1 \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "수정된 내용"
  }'

# 여러 필드 수정
curl -X PATCH http://localhost:8082/api/boards/1 \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "수정된 제목",
    "content": "수정된 내용",
    "boardImage": {
      "imageId": 2
    }
  }'
```

---

## ❌ 에러 코드

| 에러 코드 | 메시지 | 설명 |
|---------|-------|------|
| BOARD001 | 게시글을 찾을 수 없습니다 | 존재하지 않는 게시글 |
| BOARD002 | 권한이 없습니다 | 작성자가 아님 |
| IMAGE001 | 이미지를 찾을 수 없습니다 | 존재하지 않는 이미지 |
| IMAGE002 | 이미지 업로드에 실패했습니다 | 파일 저장 실패 |
| IMAGE003 | 지원하지 않는 이미지 형식입니다 | 이미지 파일이 아님 |

---

## ⚙️ 설정

### application.yml
```yaml
# File Upload
spring:
  servlet:
    multipart:
      enabled: true
      max-file-size: 10MB      # 최대 파일 크기
      max-request-size: 10MB   # 최대 요청 크기

file:
  upload-dir: uploads/board-images  # 업로드 디렉토리
```

---

## 📝 참고사항

1. **이미지 파일 형식**: JPG, PNG, GIF, WEBP 등 이미지 형식만 업로드 가능
2. **최대 파일 크기**: 10MB
3. **파일 저장**: 서버의 `uploads/board-images/` 디렉토리에 UUID로 저장
4. **권한 확인**: 게시글 수정/삭제는 작성자만 가능
5. **이미지 선택 사항**: 게시글 작성 시 이미지는 필수가 아님
