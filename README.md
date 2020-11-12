# Momelet
Momelet은 다수와 식당 및 메뉴를 고민할 때 1분 30초 이내에 식당 및 메뉴 결정을 도와주는 서비스입니다.

# 브랜치 전략
master 브랜치는 출시된 소스코드를 관리
주요 기능을 master 브랜치에서 feature 브랜치로 뽑아서 개발 후 머지 리퀘스트 진행
master 브랜치에 push 이벤트 발생시 jenkins 와 AWS codedepoly를 통해 ec2에 자동 배포됨

# 백엔드 API 서버
Spring Boot로 개발한 REST API 서버의 소스코드.
주요기능은 아래와 같음.

1. OAuth2 소셜 로그인 (구글, 카카오, 네이버, 애플)
2. 유저의 위치, 취향 정보에 맞는 식당 정보 제공
3. 유저 이름, 카테고리, 프로필 사진 수정기능 
4. 식당카드 스와이프 게임에서 카드 제공
5. 식당 북마크 생성/삭제/조회 기능
6. 메뉴 생성/수정/삭제 기능
7. 식당 사진 조회/업로드 기능
8. 식당 정보 수정 요청 생성/조회/삭제 기능
9. 식당 검색 기능

# 사용된 기술
1. spring security
2. spring data jpa / jpa / hibernate / quesydsl
3. CI/CD : docker, jenkins, codedeploy, s3
4. 모니터링: spring actuator, prometheus, grafana