| HTTP 메서드 | URL                | 설명                  | Request        | Success Response        | Error Response        |
|-------------|--------------------|-----------------------|------------------|------------------|------------------|
| POST         | /auths/signup       | 사용자 회원가입   | {<br>"email": "string",<br>"password": "string",<br>"name": "string",<br>"companyName": "string"}             | `{ "message": "Hello" }` | `{ "message": "Hello" }` |
| POST        | /auths/signin      | 사용자 로그인 | `{ "name": "A" }` | `{ "id": 1, "name": "A" }` | `{ "message": "Hello" }` |
| POST      | /auths/signout | 사용자 로그아웃         | 없음             | `{ "message": "삭제 완료" }` | `{ "message": "Hello" }` |
| GET      | /auths/user | 회원 정보 확인         | 없음             | `{ "message": "삭제 완료" }` | `{ "message": "Hello" }` |
| PUT      | /auths/user | 회원 정보 수정         | 없음             | `{ "message": "삭제 완료" }` | `{ "message": "Hello" }` |
