package jpabook.jpashop.repository;

import java.util.List;
import javax.persistence.EntityManager;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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

}
