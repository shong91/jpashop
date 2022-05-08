package jpabook.jpashop.repository.order.simplequery;

import java.util.List;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * Repository 는 가급적 순수 Entity 를 조회하는 용도로,
 * <p>
 * SimpleQueryRepository 는 통계 등 복잡한 쿼리를 직접 사용하여야 하는 조회 전용 레파지토리로 사용.
 * <p>
 * (화면 dependency 가 높고 쿼리 복잡도가 높은 경우)
 * <p>
 * 이와 같이 분리하여 사용하여야 유지보수 용이!
 */
@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

  private final EntityManager em;

  public List<OrderSimpleQueryDto> findOrderDtos() {
    // fetch join 대비, join 시 원하는 필드만 가져오도록 함
    // DB -> 애플리케이션 네트워크 용량 최적화 (but 생각보다는 미비함..)
    // 리포지토리 재사용성 떨어짐 (해당 API spec 에만 맞춰져있음)
    return em.createQuery(
        "select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address) from Order o "
            + "join o.member m " + "join o.delivery d",
        OrderSimpleQueryDto.class).getResultList();
  }
}
