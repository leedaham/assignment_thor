
# T Backend Test

## 문제 설명

Spring Boot 및 RDBMS를 사용하여 디렉토리 API를 구현하세요.

### 필수 고려사항

- 가능한 모든 **Edge Case**를 고려하여 작성할 것
    - 예: 디렉토리 추가 시 부모 디렉토리가 존재하지 않으면 실패 처리하거나, 쿼리 스트링 옵션을 통해 부모 디렉토리도 자동 생성 가능
- **Integration Test** 및 **Unit Test**를 반드시 포함

---

## API 명세

### 1. 디렉토리 추가 API

- **Endpoint**: `/directory/create`
- **Method**: POST
- **Request Body**:

```json
{
  "pathName": "/abc/name",
  "owner": "root",
  "group": "rootGroup",
  "permissions": "rwxrwxr---",
  "size": 100
}
```

---

### 2. 디렉토리 이름 변경 API

- **Endpoint**: `/directory/rename`
- **Method**: POST
- **Request Body**:

```json
{
  "oldPathName": "/abc/old",
  "newName": "new"
}
```

---

### 3. 디렉토리 이동 API

- **Endpoint**: `/directory/move`
- **Method**: POST
- **Request Body**:

```json
{
  "sourcePath": "/abc/old",
  "targetPath": "/def/new"
}
```

---

### 4. 디렉토리 정보 조회 API

- **Endpoint**: `/directory/info/%2Fabc%2Fefg`
- **Method**: GET
- **Response Body**:

```json
{
  "pathName": "/abc/efg",
  "owner": "root",
  "group": "rootGroup",
  "permissions": "rwxrwxr---",
  "size": 100,
  "created": "20220926112640",
  "modified": "20220926112640"
}
```

---

### 5. 서브 디렉토리 리스트 조회 API

- **Endpoint**: `/directory/sublist/%2Fabc`
- **Method**: GET
- **Response Body**:

```json
{
  "name": "/abc",
  "subDirectories": [
    {
      "name": "/abc/efg",
      "subDirectories": [
        {
          "name": "/abc/efg/hij",
          "subDirectories": []
        },
        {
          "name": "/abc/efg/mnl",
          "subDirectories": []
        }
      ]
    },
    {
      "name": "/abc/qwer",
      "subDirectories": []
    }
  ]
}
```

---

### 6. 디렉토리 제거 API

- **Endpoint**: `/directory/remove/%2Fabc`
- **Method**: DELETE

---

# 문제 풀이

> **Spring boot, JPA, Querydsl, H2**
### Work Flow
> *Client* <-(HTTP)-> **Controller** <-(DTO)-> **Service** <-(Entity, parameter)-> **Repository** <-(Entity)-> *DB*

### 디렉토리 추가 API
> [POST] /directory/create
  - <a href="https://github.com/leedaham/assignment_thor/tree/master/src/main/java/me/hamtom/thor/directory/domain/create">Create package</a>

**옵션 쿼리 스트링**
1) createMissingParent: 디렉토리 추가시 부모 디렉토리가 존재하지 않는 경우, 모두 생성하는 옵션
2) flexibleCapacity: 디렉토리 추가시 용량이 부족할 경우, 남은 용량에 맞춰 생성하는 옵션

### 디렉토리 이름 변경 API
> [POST] /directory/rename
  - <a href="https://github.com/leedaham/assignment_thor/tree/master/src/main/java/me/hamtom/thor/directory/domain/migrate">Migrate package</a>

**옵션 쿼리 스트링**
1) mergeOnDuplicate: 변경하고자 하는 경로에 이미 디렉토리가 존재하는 경우, 병합하는 옵션

### 디렉토리 이동 API
> [POST] /directory/move
- <a href="https://github.com/leedaham/assignment_thor/tree/master/src/main/java/me/hamtom/thor/directory/domain/migrate">Migrate package</a>

**옵션 쿼리 스트링**
1) mergeOnDuplicate: 이동하고자 하는 경로에 이미 디렉토리가 존재하는 경우, 병합하는 옵션

### 디렉토리 조회 API
> [GET] /directory/info/{pathName}
- <a href="https://github.com/leedaham/assignment_thor/tree/master/src/main/java/me/hamtom/thor/directory/domain/info">GetInfo package</a>

### 서브 디렉토리 조회 API
> [GET] /directory/sublist/{pathName}
- <a href="https://github.com/leedaham/assignment_thor/tree/master/src/main/java/me/hamtom/thor/directory/domain/sublist">GetSublist package</a>

### 디렉토리 제거 API
> [DELETE] /directory/remove/{pathName}
- <a href="https://github.com/leedaham/assignment_thor/tree/master/src/main/java/me/hamtom/thor/directory/domain/remove">Remove package</a>

**옵션 쿼리 스트링**
1) removeWithChild: 삭제하고자 하는 디렉토리의 자식 디렉토리가 존재하는 경우, 모두 삭제하는 옵션

### Common
- <a href="https://github.com/leedaham/assignment_thor/blob/master/src/main/java/me/hamtom/thor/directory/domain/common">Common package</a>

### Config
- <a href="https://github.com/leedaham/assignment_thor/tree/master/src/main/java/me/hamtom/thor/directory/config">Config package</a>

### Test
- <a href="https://github.com/leedaham/assignment_thor/tree/master/src/test/java/me/hamtom/thor/directory">Test package</a>
