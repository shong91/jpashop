# API 개발 고급 정리

## 조회 방식

### 엔티티 조회 방식

- 엔티티로 직접 조회 (v1)
- 엔티티로 조회 후 DTO로 변환 (v2)
- fetch join 으로 쿼리 수 최적화 (v3)
- 컬렉션 페이징과 한계 돌파 (v3.1)
    - 컬렉션은 fetch join 불가
    - fetch join 으로 쿼리 수 최적화 + 컬렉션은 지연 로딩 유지 (default_batch_fetch_size)

### DTO 직접 조회 방식

- JPA에서 DTO를 직접 조회 (v4)
- 컬렉션 조회 최적화 (v5)
    - OneToMany 컬렉션은 IN 절 활용
- 플랫 데이터 최적화 (v6)
    - JOIN 결과 그대로 조회 후 애플리케이션 단에서 원하는 DTO로 변환

## 권장 순서

엔티티 조회 방식을 권장하며, 불가피할 경우 2, 3 의 다른 방식을 선택하자.

**why? 엔티티 방식은 코드를 수정하지 않고, 옵션만 약간 변경하여 다양한 성능 최적화를 시도할 수 있다.**

DTO 직접 조회 시, 성능 최적화하거나 성능 최적화 방식을 변경할 때 많은 코드를 변경하여야 한다. (SQL mapper 와 거의 같은 방식)

1. 엔티티 조회 방식으로 우선 접근

- fetch join 으로 쿼리 수 최적화
- 컬렉션 최적화
    - 페이징 필요: default_batch_fetch_size
    - 페이징 불필요: fetch join 사용

2. 엔티티 방식으로 안되면 DTO 조회 방식 사용
3. DTO 방식으로 안되면 NativeSQL or Spring JdbcTemplate 
