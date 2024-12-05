| HTTP 메서드 | URL                | 설명                  | 요청 본문        | Success Response        | Error Response        |
|-------------|--------------------|-----------------------|------------------|------------------|------------------|
| GET         | `/api/hello`       | Hello 메시지를 반환   | 없음             | `{ "message": "Hello" }` | `{ "message": "Hello" }` |
| POST        | `/api/create`      | 새로운 데이터를 생성 | `{ "name": "A" }` | `{ "id": 1, "name": "A" }` | `{ "message": "Hello" }` |
| DELETE      | `/api/delete/{id}` | 데이터를 삭제         | 없음             | `{ "message": "삭제 완료" }` | `{ "message": "Hello" }` |
