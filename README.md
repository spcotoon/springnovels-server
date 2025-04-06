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

<p>1. EC2 CPU 병목</p>

```
ps aux | grep java
>>>pid 확인

top -H -p 1234(pid)

ps -L -o pid,tid,pcpu,cmd -p 1234(내 pid) --sort=-pcpu | head -n 20
>>>tid 확인

Java 스레드 덤프에서 스레드는 16진수 (hexadecimal) 로 표시되므로, 찾은 TID를 16진수로 변환해야 한다.
printf "%x\n" 5678(여긴 tid)
예시 -> 303a 이게 tid

jstack 1234(pid) > threaddump.txt

grep -A 20 "nid=0x303a" threaddump.txt
```

* 높은 CPU 사용율을 보이는 쓰레드들은 BCrypt를 수행하고 있었다.
* 한번에 많은 로그인 요청이 발생하며 로그인 처리 단계에서 이미 많은 부하가 발생하여 소설 조회로 넘어가는 것도 버벅이는 상황.
* EC2 2대로도 부하가 잡히질 않아 3대로 증설 후 DB 병목으로 넘어감.
<br/><br/><br/>

<p>2. 락 경합</p>
<p>- 모니터링으로 RDS의 CPU 사용이 98%에 도달한 것을 확인 LOCK 경합을 원인으로 추론.</p>
<p>- redis로 락을 관리하여 부하 분산을 시도하였다. </p>
<p>- 아직 맛만 봐본 낮은 redis 숙련도로.. Redisson을 활용해 Lock 관리를 구현하는 것까지로 이번 프로젝트는 만족하였다.</p>
<br/><br/><br/>

<p>3. 소설 열람시 멤버, 작가, 소설 엔티티와 구매 내역 조회에 4개의 쿼리가 나가며 DB에 병목</p>

``` java
@Getter
public class NovelContextDto {

    private Member member;
    private Author author;
    private Novel novel;
    private Boolean isRead;

    @Builder
    private NovelContextDto(Member member, Author author, Boolean isRead, Novel novel) {
        this.member = member;
        this.author = author;
        this.novel = novel;
        this.isRead = isRead != null ? isRead : false;
    }
}
```
* 조회에 필요한 엔티티를 모두 담은 DTO를 만들어 JOIN문으로 한번에 담아 메서드에서 Getter로 각 엔티티로 분리

```
-> Nested loop left join  (cost=101 rows=1002) (actual time=0.35..0.351 rows=1 loops=1)
    -> Rows fetched before execution  (cost=0..0 rows=1) (actual time=74e-6..115e-6 rows=1 loops=1)
    -> Filter: ((ph.member_id = 1) and (ph.novel_id = 1234))  (cost=101 rows=1002) (actual time=0.348..0.348 rows=0 loops=1)
        -> Table scan on ph  (cost=101 rows=1002) (actual time=0.0427..0.282 rows=1002 loops=1)
```
* 쿼리 수는 줄었지만 한번의 조회에 350ms 정도 소요. -> 소설, 작가는 이미 pk로 조회하기 때문에 구매내역에 커버링인덱스 시도

```
CREATE INDEX idx_ph_member_novel_isread
ON purchase_history(member_id, novel_id, is_read);

-> Nested loop left join  (cost=0.725 rows=1) (actual time=0.00797..0.00811 rows=1 loops=1)
    -> Rows fetched before execution  (cost=0..0 rows=1) (actual time=66e-6..107e-6 rows=1 loops=1)
    -> Covering index lookup on ph using idx_ph_member_novel_isread (member_id=500, novel_id=1234)  (cost=0.725 rows=1) (actual time=0.00622..0.00622 rows=0 loops=1)
```

* 인덱스에서 필요한 데이터를 바로 조회하며 8 ~ 10ms로 대폭 줄어들었다.
<br/><br/>

[결론]
* 최초 부하 테스트

![image](https://github.com/user-attachments/assets/f0266beb-3307-4309-8d7c-67011fe5dbf2)

* 100만건의 소설을 테이블 풀스캔으로 조회하기 때문에 사용이 불가능한 홈페이지 상태이다.
* -> 페이지네이션과 미리 테이블을 최신순으로 정렬한 인덱스 설정 


<br/><br/>
* 중간 테스트 (최초 테스트는 기본적인 최적화도 안된 상태로 진행한 것이기 때문에 여기부터가 진짜 테스트)

![image (1)](https://github.com/user-attachments/assets/f77c9836-ebcd-43e6-993f-ec8b95785612)

* EC2 CPU에서 병목발생 -> 많은 로그인 시도를 원인으로 추론 -> EC2 3대 운영으로 요청 분산
 - 1대 추가 → TPS 21 / Latency 900ms/ EC2 CPU 99% / RDS CPU 75%
 - 2대 추가 → TPS 26 / Layency  800ms/ EC2 CPU 70% / RDS CPU 97%

<br/><br/>
* 최종 테스트

![image (2)](https://github.com/user-attachments/assets/97469acc-7437-476a-ac85-b2ec40ab20d3)

* 쿼리 최적화, Redisson 활용으로 DB로부터 락 경합 분산
* 최종 결과 TPS 13.8/s -> 28.7/s / Latency 1000ms -> 523ms
<br/><br/><br/>


[앞으로 고민해볼 개선점]

- 쿼리수를 줄이고 LOCK에 대한 부담도 Redisson을 활용하며 성능이 크게 향상될 것으로 기대하였으나 한 번에 4개씩 나가는 update, insert 쿼리가 여전히 rds에 과한 부담을 주는 것으로 보입니다.
1. 코인 거래에 대한 3개의 쿼리는 실시간 반영이 중요하나 프론트엔드에 표시하기 위한 조회수는 실시간에 대한 중요도가 다소 낮다고 판단하여 redis에 조회수를 따로 업데이트하고 주기적으로 DB에 반영. 
2. 3대의 서버에서 redis - DB반영 시점에 대한 동시성 고려.
3. 지금 시나리오는 한 작품에 대한 테스트지만 실제 상황에선 동시에 여러 작품에 요청이 들어올 것이기 때문에 redis를 효율적으로 관리 하기 위해 novel 엔티티에 today 필드를 만들어 많은 요청이 있을 오늘의 게시글만 레디스에 조회수 업데이트, 상대적으로 부하가 적을 이전 소설들은 지금처럼 rds에 직접 업데이트.

<br/><br/>

[프로젝트 후기]

- 3월 22일로부터 계획했던 2주가 되어 목표로 했던 TPS 50/s 까지는 도달하지 못했지만 처음으로 부하 테스트를 진행하며 운영에 대한 고민을 할 수 있는 기회가 되었습니다.
- 빠르게 테스트 단계로 넘어 오기 위해 프로젝트는 매우 단순하게 구성하여 다루기가 비교적 편했지만 필드 변수 수부터 구성까지 훨씬 복잡할 실무 환경을 생각할수록 CS 지식과 기본기에 대한 중요성을 크게 느꼈습니다.
