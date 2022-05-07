package jpabook.jpashop.repository;

import java.util.List;
import javax.persistence.EntityManager;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

  private final EntityManager em;

  public void save(Item item) {
    // JPA 에 저장하기 전까지는 id 값이 생성되지 않음
    if (item.getId() == null) {
      em.persist(item);
    } else {
      // 단순한 경우에는 merge 를 사용하는 것이 편할 수 있으나, null update 위험성 때문에 실무에서는 비권장.
      // merge 가 영속성 컨텍스트에서 관리되며, 파라미터로 받은 item 은 영속성 관리대상 아님.
      Item merge = em.merge(item);
    }
  }

  public Item findOne(Long id) {
    return em.find(Item.class, id);
  }

  public List<Item> findAll() {
    return em.createQuery("select i from Item i", Item.class).getResultList();
  }
}
