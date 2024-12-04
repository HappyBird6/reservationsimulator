### 프로젝트 소개
<hr>

RabbitMQ를 이용한 간단한 예약 대기열 구현<br>
RabbitMQ 환경 세팅 및 학습용

### 기간 / 인원
<hr>

### 기능
<hr>

1. 예매 입장 대기열
	+ Polling으로 대기열 정보 전달 받음
	+ 정해진 인원만이 동시에 예약 가능 -> 트래픽 조절
	+ 대기열 번호, 예약 가능 인원수의 카운트 와 좌석 예약을 atomic.class를 사용해 동기화 이슈 방지
	+ 사용자 예약 대기열 정보는 caffein cahce로 서버 로컬 캐시에 임시 저장
	+ 메세지 리스너에서 현재 예약 중인 인원을 while 루프문으로 1초마다 조건을 비교해 리스너의 종료를 막고 예약 조건이 충족 되면 리스너를 종료해 다음 대기자의 메세지를 받는다.(Service/ReservationOrderService.java processReservationOrder())

<아키텍처>
![RabbitMQ reservation 아키텍처](https://github.com/user-attachments/assets/cb95cb52-c1da-4680-a2fd-787553676a8b)
<시작화면>
![sample_reservation_1](https://github.com/user-attachments/assets/9a7fe3a4-defb-4a15-9dea-a18f00a8b6fd)
동시예약 인원 3명인 상태에서 순번이 1인 상태
![sample_reservation_2](https://github.com/user-attachments/assets/ed64c667-db0f-45c3-9546-c823e657fec2)
Polling으로 예약가능 상태 정보를 받고 예약 가능 상태로 변경됨
![sample_reservation_3](https://github.com/user-attachments/assets/5fb587c4-17d3-4848-869c-4bebd7de00ec)
B(10002) 좌석을 신청완료
![sample_reservation_4](https://github.com/user-attachments/assets/ea569f12-505a-4d0b-9674-6b412ea3266c)

### 기술
<hr>

+ 언어 : JAVA
+ 프레임워크 : SpringBoot
+ 라이브러리 : Caffeine cache
+ 도구 : RabbitMQ

### 학습 사항
<hr>

+ RabbitMQ 세팅 및 환경 설정
+ RabbitMQ와 같은 메세지큐의 사용방법 습득 및 사고 확장


### 트러블 / 피드백
<hr>

+ Polling을 이용해 서버와 통신했지만 서버 자원 측면에서는 Long Polling으로 바꾸는 것이 더 효율적
+ 대기열 서버와 예약 서버를 따로 나누어야함. localhost 환경에서만 진행했기때문에 서버를 나누지는 않음.
