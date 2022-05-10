package jpabook.jpashop.repository;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

  private final EntityManager em;

  public void save(Order order) {
    em.persist(order);
  }

  public Order findOne(Long id) {
    return em.find(Order.class, id);
  }

  public List<Order> findAll(OrderSearch orderSearch) {
    // 정적쿼리
    return em.createQuery("select o from Order o join o.member m "
            + "where o.status = :status"
            + "and m.name like :name", Order.class)
        .setParameter("status", orderSearch.getOrderStatus())
        .setParameter("name", orderSearch.getMemberName())
        .setFirstResult(100) // 페이징 시 변수 설정
        .setMaxResults(1000)
        .getResultList();

    // 동적쿼리 만들기
    // 1. 직접 jpql string 생성
    // 2. JPA criteria 를 통한 jpql 생성
    // 3. Querydsl (권장)
  }


  public List<Order> findAllByString(OrderSearch orderSearch) {

    String jpql = "select o from Order o join o.member m";
    boolean isFirstCondition = true;

    //주문 상태 검색
    if (orderSearch.getOrderStatus() != null) {
      if (isFirstCondition) {
        jpql += " where";
        isFirstCondition = false;
      } else {
        jpql += " and";
      }
      jpql += " o.status = :status";
    }

    //회원 이름 검색
    if (StringUtils.hasText(orderSearch.getMemberName())) {
      if (isFirstCondition) {
        jpql += " where";
        isFirstCondition = false;
      } else {
        jpql += " and";
      }
      jpql += " m.name like :name";
    }

    TypedQuery<Order> query = em.createQuery(jpql, Order.class)
        .setMaxResults(1000);

    if (orderSearch.getOrderStatus() != null) {
      query = query.setParameter("status", orderSearch.getOrderStatus());
    }
    if (StringUtils.hasText(orderSearch.getMemberName())) {
      query = query.setParameter("name", orderSearch.getMemberName());
    }

    return query.getResultList();
  }

  /**
   * JPA Criteria
   */
  public List<Order> findAllByCriteria(OrderSearch orderSearch) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Order> cq = cb.createQuery(Order.class);
    Root<Order> o = cq.from(Order.class);
    Join<Object, Object> m = o.join("member", JoinType.INNER);

    List<Predicate> criteria = new ArrayList<>();

    //주문 상태 검색
    if (orderSearch.getOrderStatus() != null) {
      Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
      criteria.add(status);
    }
    //회원 이름 검색
    if (StringUtils.hasText(orderSearch.getMemberName())) {
      Predicate name =
          cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName() + "%");
      criteria.add(name);
    }

    cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
    TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);
    return query.getResultList();
  }

  public List<Order> findAllWithMemberDelivery() {
    // 기술적으로는 sql 의 inner join 과 동일.
    // order 조회 시 member, delivery 의 데이터도 한 번에 가져오도록 함
    return em.createQuery(
        "select o from Order o" + "join fetch o.member m" + "join fetch o.delivery d",
        Order.class).getResultList();
  }

  public List<Order> findAllWithItem() {
    // 1대다 조인에서 데이터베이스 row 수가 증가하게 되어, 결과값(id값) 은 갗ㅌ은 order 엔티티의 조회 수도 증가하게 된다.
    // JPA 의 distinct 는 SQL 의 distinct 키워드를 추가하고,
    // 같은 엔티티가 조회되면, 애플리케이션에서 중복을 걸러주는 역할을 하여 컬렉션 fetch join 의 중복 조회를 막아준다.

    // [Collection fetch join 의 단점]
    // 1. 1대다 관계의 엔티티를 fetch join 할 시 페이징이 불가능하다 !!!
    // (애플리케이션 단으로 DB 데이터를 모두 올린 다음, 메모리에서 페이징 처리 => out of memory issue 발생)

    // 2. 컬렉션 페치 조인은 1개만 사용할 수 있다 !!!
    // 컬렉션 둘 이상에 페치 조인을 사용할 경우(1*N*M?), 데이터가 부정합하게 조회될 수 있다.

    return em.createQuery("select distinct o from Order o " +
            "join fetch o.member m " +
            "join fetch o.delivery d " +
            "join fetch o.orderItems oi " +
            "join fetch oi.item i ", Order.class)
        .getResultList();
  }
}
