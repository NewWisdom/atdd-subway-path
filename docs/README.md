## 기능 목록

### 1단계

#### 프론트엔드

- [x] 토큰 발급 요청 API 구현하기

#### 백엔드

- [x] 토큰 발급 API 구현하기
- request

```json
POST /login/token HTTP/1.1
content-type: application/json; charset=UTF-8
accept: application/json
{
    "password": "password",
    "email": "email@email.com"
}
```

- response

```json
HTTP/1.1 200
Content-Type: application/json
Transfer-Encoding: chunked
Date: Sun, 27 Dec 2020 04:32:26 GMT
Keep-Alive: timeout=60
Connection: keep-alive

{
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlbWFpbEBlbWFpbC5jb20iLCJpYXQiOjE2MDkwNDM1NDYsImV4cCI6MTYwOTA0NzE0Nn0.dwBfYOzG_4MXj48Zn5Nmc3FjB0OuVYyNzGqFLu52syY"
}
```

- [x] cors 처리 설정

### 2단계

#### 프론트엔드

- [x] 내 정보 기능에 관한 API 호출 기능 구현하기

### x 백엔드

- [x] 토큰을 통한 인증 - 내 정보 기능
    - [x] "/members/me" 요청 시 토큰을 확인하여 로그인 정보를 받아오기
    - [x] @AuthenticationPrincipal과 AuthenticationPrincipalArgumentResolver을 활용



## 다시 구현한다면?

#### ArgumentResolver 에서 DTO인 LoginMember를 반환하게 할걸!

* 도메인을 반환하면 뷰와 컨트롤러에 노출시키고 도메인을 조작할 가능성이 있음
* 또 불필요한 pw 필드도 가지게 됨



#### 비번 암호화와 member 객체에서 비번을 확인하게 할걸!

```java
public TokenResponse createToken(TokenRequest tokenRequest) {
    Member member = memberDao.findByEmailAndPassword(tokenRequest.getEmail(), tokenRequest.getPassword())
            .orElseThrow(() -> new AuthorizationException("로그인 실패입니다."));
    String accessToken = jwtTokenProvider.createToken(String.valueOf(member.getId()));
    return new TokenResponse(accessToken);
}
```

* 비번을 암호화 할 때는 Spring Security의 `BCryptPasswordEncoder()` 를 사용
* 회원가입할 때 입력한 비번을 인코딩하여 저장
* 그런데 만약 여기서 로그인할 때 입력한 비번을 `encoder.encode()` 하여 조회하면 실패
* 왜냐면 동일한 원문이어도 각기 다른 인코딩된 값을 내뱉기 때문

```java
PasswordEncoder encoder = new BCryptPasswordEncoder();
String 원문 = "qwe123";
String 암호화된_원문 = encoder.encode(원문);
String 암호화된_원문_2 = encoder.encode(원문);

encorder.matches(원문, 암호화된 원문); // true
암호화된_원문.equals(암호화된_원문2); //false
```

* 때문에 쿼리로 Member를 받아오고 이 객체에서 `encorder.matches()`를 이용해 비번을 확인하자



#### 최단거리 찾는 로직을 전략 패턴으로 구현할걸!

* 도메인에 외부 라이브러리를 가지고 있는 것이 옳은가?
* 만약 알고리즘이 변경되면 도메인에도 영향이 감
* 전략패턴으로 Dijkstra 알고리즘을 주입하자



CredentialRequest

세션으로 로그인 처리 브라우저에 쿠키가 있으면 자동으로 실어보냄

