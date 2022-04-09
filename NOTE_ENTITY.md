# 엔티티 설계 시 주의점

1. Entity 에는 가급적 @Setter 를 사용하지 말자

- Setter 가 열려 있으면 변경 포인트가 너무 많아짐 => 유지보수 어려움

2. 모든 연관관계는 지연로딩으로 설정 ! (**중요**)

- 즉시로딩는 예측이 어렵고, 어떤 SQL 이 실행될지 추적하기 어렵다.
    - 즉시로딩(`EAGER`): 테이블을 로딩하는 시점에 관계가 매핑된 연관 테이블을 함께 로딩
    - 지연로딩(`LAZY`): 선택한 테이블만 로드
- 특히, JPQL 실행 시 N+1 문제
    - ex) em.find() 로 1건을 가져오려할 때, EAGER 설정으로 다른 테이블과 매핑되어 있다면 1건을 가져오기 위해 매핑 테이블의 모든 값(N) 을 조회하게 됨
- 연관된 엔티티를 함께 DB에서 조회해야 할 때에는 fetch join, entity graph 기능을 사용한다.
- @XToOne(ManyToOne, OneToOne) 관계는 기본이 즉시로딩이므로, 직접 지연로딩으로 설정해야 한다.

3. 컬렉션은 필드에서 초기화 하자

- 필드에서 바로 초기화 하는 것이 안전 (null 문제 등)
- hibernate 는 엔티티를 영속화 할 때, 컬렉션을 감싸서 hibernate 가 제공하는 내장 컬렉션으로 변경한다. hibernate 내부 매커니즘에 문제가 발생할 수 있기
  때문에, 필드 레벨에서 생성하는 것이 안전하고, 코드도 간결하다.

4. 테이블, 컬럼명 생성 전략

- 적용 1단계) 스프링부트 기본 전략 - SpringPhysicalNamingStrategy
- 적용 2단계) 논리명, 물리명 생성 - ImplicitNamingStrategy, PhysicalStrategy

5. CASCADE 설정

- 연관관계가 있는 엔티티에 대하여 연쇄적인 작업이 가능하도록 함 (Order-Item, Order-Delivery)

6. 연관관계 편의 메서드 활용

- Order, Category 참조
