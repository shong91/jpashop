package jpabook.jpashop.repository;

import java.util.List;
import jpabook.jpashop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

  // 별도 구현체를 생성하지 않아도, JPA 가 구현체를 애플리케이션 실행시점에 주입해준다.
  // findBy + field 명의 메서드명을 기반으로 [ select m from Member m where m.name = ? ] 이라는 jpql 을 생성하여 쿼리한다.
  List<Member> findByName(String name);

}
