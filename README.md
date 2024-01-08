# ThorDrive Backend Test
> **Spring boot, H2**
- 생각할 수 있는 Edge case를 모두 고려하여 작성해주시기 바랍니다.
  Edge case를 처리하기 위해 쿼리 스트링 등을 사용해도 좋습니다.
  예시) 디렉토리 추가 시 부모 디렉토리가 존재하지 않는 경우, 부모 디렉토리까지 모두 생성하라는 옵션을 쿼리 스트링으로 주어 모두 생성 및 성공 처리 또는 옵션이 없는 경우 부모 디렉토리가 존재하지 않아 디렉토리 생성에 실패했다는 실패 코드 및 실패 메시지 리턴.
- Integration test 및 Unit test를 포함하여 제출해주시기 바랍니다.

### Table
#### Directory
| Column name | Type  | Description  |
|:------------|:-----:|:-------------|
| id          | Long  | id           |
| pathName    |||
| owner       ||
| group       ||
| permissions ||
| size        ||
| created     ||
| modified    ||

#### Environment
| Column name      | Type  | Description  |
|:-----------------|:-----:|:-------------|
| totalCapacity    |||
| currentCapacity  |||

### 디렉토리 추가 API
> [POST] /directory/create

#### 필수 파라미터
- pathName: String
- owner: String
- group: String
- permissions: String
- size: int

#### 옵션 파라미터
- createMissingParent: String > Y/N
- 사이즈가 부족하다면 맞춰서 생성?

#### 검증
##### Controller
- pathName 유효성 확인
- owner, group 이름 유효성 확인
- permissions 유효성 확인
- size 최소, 최대값 확인

##### Service
- pathName 이름 확인
- size 남은 공간 확인

### 디렉토리 이름 변경 API
> [POST] /directory/rename

#### 필수 파라미터
- oldPathName: String
- newName: String

#### 옵션 파라미터
mergeIfDuplicateName: String > Y/N

#### 검증
##### Controller
- pathName 유효성 확인
- newName 유효성 확인 (위랑 달라야 하나?)

