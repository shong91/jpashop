package jpabook.jpashop.repository;

import java.util.List;
import javax.persistence.EntityManager;
import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

  // @PersistenceContext 사용. @Autowired 를 사용해도 동일하게 동작함 (Spring 지원)
  // @PersistenceUnits() 으로 entityManagerFactory 를 설정할 수도 있다.
  private final EntityManager em;


  public void save(Member member) {
    em.persist(member);
  }

  public Member findOne(Long id) {
    // 영속성 컨텍스트에 엔티티가 올라가는데, PK 를 키로 영속성 컨텍스트에 값을 저장한다.
    return em.find(Member.class, id);
  }

  public List<Member> findAll() {
    // JPQL: entity 에 대한 쿼링
    return em.createQuery("select m from Member m", Member.class).getResultList();
  }

  public List<Member> findByName(String name) {
    return em.createQuery("select m from Member m where m.name = :name", Member.class)
        .setParameter("name", name).getResultList();
  }
}
