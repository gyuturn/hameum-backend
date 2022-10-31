
# Troll911
- "League of Leagend"게임에서 유저의 전적검색 및 트롤검색 기능을 제공합니다.
- 머신러닝을 이용하여 유저의 트롤확률을 알아낼 수 있습니다.
- 일반적인 전적검색/멀티서치가 제공됩니다.
- 5*5 랭크게임포인트를 기준으로 랭킹을 제공합니다.
- 트롤확률을 기반으로 랭킹을 제공합니다.
- 카카오를 통한 로그인 서비스를 제공합니다.
- 회원유저 한정하여 듀오를 찾을 수 있으며 서비스 내에서 채팅기능을 제공합니다.
- https://www.youtube.com/watch?v=vtUlxVK2w5E (프로젝트 소개영상 링크)
- 현재 서버비용문제로 서버는 닫은상태입니다.

 

# About Project
### URL
- front
  - http://3.37.22.89:3000
- swagger
  - http://3.37.22.89:8080/swagger-ui/index.html
  - 스웨거를 통한 비지니스 로직 API 관리
- jenkins
  - http://3.37.22.89:8090
  - CI/CD 파이프라인 구성 용도
  - WebHook을 걸어 자동화 설정 완료
  - front, backend, dataflow 세개의 APPLICATION에대해서 CI/CD를 관리
  - Docker를 활용한 APPLICATION을 컨테이너화 하여 관리


### 서비스 구성도

![image](https://user-images.githubusercontent.com/87477702/197344167-656008ca-6af0-460a-8c64-0476e54ba972.png)
- Front
  - 유저에게 입력을 받고 데이터를 back단으로 전달해주는 역할을 한다.
  - 유저에게 화면을 제공한다.
  - Request에 대한 Response를 가공하여 유저가 요청한 데이터값을 표시한다.
- Back(비지니스 로직처리)
  - Front로부터 받은 Reqeust에대해 알맞음 Response를 보낸다.
  - 회원가입/로그인/전적검색등 위에서 제공되는 서비스에 대한 Response를 관리한다.
- Back(DataFlow)
  - RiotAPI 호출량 문제로인해 백단에서 주기적으로 RiotAPI 호출하여 유저의 전적을 DB에 저장한다.
- RiotAPI
  - 랭킹조회시 사용된다.
  - 유저의 이름을 이용하여 해당 API를 호출하여 유저의 전적을 저장한다.(DataFlow에서 처리)
- DB
  - 회원가입을한 유저의 정보
  - 머신러닝을 위해 필요한 데이터 저장 및 제공
  - 유저의 게임 전적저장
- ML+FLASK API SERVER
  - DB를 통해 얻어온 데이터를 가공 및 분석하여  '회기분석'을  사용해 트롤점수를 구현한다.
  - DB를 통해 얻어온 트롤점수를 이용하여  'KMEANS 알고리즘'을 사용해 cluster 분포 현황를 반환한다.
  - FLASK는 머신러닝을 이용하기 위한 Python API SERVER이다.(회기분석, KMEANS에 대한 API 제공)
- Spring Scheduler
  - 롤 API 는 API 호출에 있어 시간제약 조건이 있다
  - 그렇기에 schduler를이용하여 API 호출 제약에 효율적으로 대응한다
  - 또한 mysql에서 오래된 데이터를 자동으로 삭제한다 및 업데이트 한다.
- KafKa(Streams)
  - Scheduler는 kafka의 producer 역할을 한다. 롤 전적과 관련된 json파일을 produce 한다.
  - Scheduler로 얻은 롤 전적정보를 mysql이 consumer역할을 하여 롤 유저의 최신정 보를 반영한다
- NIFI
 - Data flow pipeline의 역할을 맡는다.
 - Spring scheduler에서 얻은 json 데이터를 mysql db에 저장하는데 까지의 data flow 를 효율적으로 활용하기 위해 사용한다.
- Docker
- Docker를 사용하여 react, springboot(비지니스 Server,DataFlow Server)를 컨테이너화 하여 관리한다
- Jenkins
•React, spring application(비지니스 APISERVER+DATAFLOW API SERVER)에서 ci/cd 자동화 역할을 한다




### 서비스 시나리오
 
![image](https://user-images.githubusercontent.com/87477702/197345125-32744119-09a8-4b26-9a93-e85af4b701c1.png)
- 비회원 사용자
  - 전적검색: 일반적인 유저의 전적을 검색한다. 추가로 머신러닝을 사용한 유저의 트롤점수 및 트롤점수를 기반으로한 듀오추천기능을 사용할 수 있다.
  - 멀티서치: 게임을 시작하면 자신을 포함한 팀원의 전적을 한번에 검 색 할 수 있다.
  - 랭킹: 5*5 솔로랭크 기준으로 상위 10명의 간단한 전적을 보여준다. 일반 리그 포인트와 트롤점수를 기반으로 한 두개의 랭킹을 볼 수 있다.
- 회원사용자
  - 마이페이지: 회원가입시 입력받은 롤 닉네임을 사용해 유저의 게임 정보 및 머신러닝을 이용한 트롤확률/듀오추천을 보여준다.(전적검색 기능과 동일)
  - 듀오찾기: 글 등록기능이 있으며, 회원유저끼리 해당 글을 통해 1:1 채팅이 가능하다. 채팅을 이용해 최종으로 두명의 유저가 듀오가 성사된다.


### WireFrame
![image](https://user-images.githubusercontent.com/87477702/197345224-42e48e0b-40dc-43dd-bee4-86e69555671b.png)


### 엔티티 관계도
![image](https://user-images.githubusercontent.com/87477702/197345274-fe10e2ed-2eb6-43ec-9dc1-1850c5037e83.png)





## Swagger
![swagger](https://user-images.githubusercontent.com/87477702/187029542-ab4fcc60-0595-4d36-b5a0-b199a65f0f17.png)
- swagger를 통한 api 문서화

## Jenkins(CI/CD)
![jenkins](https://user-images.githubusercontent.com/87477702/187029550-b389f45d-9d54-436e-95b3-73940c63eb09.png)
- jenkinsg를 통한 CI/CD
- Docker 사용


# Convention

### JAVA Convention
- **directory** : 소문자로만 작성합니다.
  - ex) main, setting
- **ClassName** : 각 단어의 첫번째 문자는 대문자로 시작합니다.
  - ex) ScoreList
- **변수 및 함수명** : 첫 단어는 소문자, 두번째 단어부터는 대문자로 시작합니다. camelCase
  - ex) mainPanel



### commit Convention
- feat: 새로운 기능 추가
- fix: 버그 수정
- docs: 문서 수정
- style: 코드 포맷팅, 세미콜론 누락, 코드 변경이 없는 경우
- refactor: 코드 리팩토링
- test: 테스트 코드, 리팩토링 테스트 코드 추가
- chore: 빌드 업무 수정, 패키지 매니징


# 참고 파일
- 2022 한이음  공모전 제작설계서_응용SW 수정본
- 2022 한이음 공모전 개발보고서 수정본
(uploadfile 참고)

