# springnovels-server
 <p>스프링코믹스에 뒤이은 소설연재 플랫폼 서버.</p>

## 🚀 기술 스택
### ⚡백엔드
<div>
<img src="https://img.shields.io/badge/spring boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
<img src="https://img.shields.io/badge/Spring JPA-59666C?style=for-the-badge&logo=hibernate&logoColor=white"> 
<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> 
<img src="https://img.shields.io/badge/security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white"> 
<img src="https://img.shields.io/badge/intelli j-000000?style=for-the-badge&logo=intellijidea&logoColor=white">
<img src="https://img.shields.io/badge/redis-FF4438?style=for-the-badge&logo=redis&logoColor=white">
</div>

### ⚡인프라
<div>
  <img src="https://img.shields.io/badge/AWS EC2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white">
  <img src="https://img.shields.io/badge/AWS ELB-8C4FFF?style=for-the-badge&logo=awselasticloadbalancing&logoColor=white">
  <img src="https://img.shields.io/badge/AWS RDS-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white">
  <img src="https://img.shields.io/badge/Elasticache-C925D1?style=for-the-badge&logo=amazonelasticache&logoColor=white">
</div>

### ⚡도구
<div>
  <img src="https://img.shields.io/badge/k6-7D64FF?style=for-the-badge&logo=k6&logoColor=white">
  <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
  <img src="https://img.shields.io/badge/postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white">
</div>


<br/>

## 💻 프로젝트 소개

### ⚡기간
2025-03-22 ~ 2025-04-04
 <br/>

### ⚡주요기능(v1)

| 역할 | 기능 |
| ----- | ----- |
|멤버|로그인 후 코인을 소비하여 소설을 열람할 수 있습니다.|
|작가|유저가 소비한 코인은 해당 소설 작성자에게 증가됩니다.|
|로그인|jwt토큰을 발급 받고 토큰을 확인하는 커스텀 필터가 포함된 시큐리티 통과|

 <br/>

### k6 부하테스트
<p>1. member 10,000명, author 100명, novel 1,000,000개 DB INSERT</p>
<p>2. novel 전체리스트(페이지네이션)를 불러오는 메인페이지 -> 로그인하여 토큰 발급 -> 소설 열람</p>
<p>3. 소설 열람시 구매 내역 확인 -> 멤버 코인 감소 -> 작가 코인 증가 -> 소설 조회수 증가 -> 열람</p>
<p>4. VUs 50, 10분간 테스트</p>


### 과제
<p>1. 소설 열람시 멤버, 작가, 소설 엔티티와 구매 내역 조회에 4개의 쿼리가 나가며 DB에 부하 상승</p>

ps aux | grep java
pid 확인

top -H -p 1234(pid)

ps -L -o pid,tid,pcpu,cmd -p 1234(내 pid) --sort=-pcpu | head -n 20
하면 tid 나옴

Java 스레드 덤프에서 스레드는 16진수 (hexadecimal) 로 표시되므로, 찾은 TID를 16진수로 변환해야 한다.
printf "%x\n" 5678(여긴 tid)
예시 -> 303a 이게 tid

jstack 1234(pid) > threaddump.txt

grep -A 20 "nid=0x303a" threaddump.txt
