package jpabook.jpashop;

import static org.junit.jupiter.api.Assertions.*;

import javax.annotation.security.RunAs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

//@RunAs(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
class MemberRepositoryTest {

  @Autowired
  MemberRepository memberRepository;

  @Test
  @Transactional
//  @Rollback(value = false)
  public void testMember() throws Exception {
    // given
    Member member = new Member();
    member.setUsername("memberA");

    // when
    Long saveId = memberRepository.save(member);
    Member findMember = memberRepository.find(saveId);

    // then
    Assertions.assertEquals(findMember.getId(), saveId);
    Assertions.assertEquals(findMember.getUsername(), member.getUsername());

    // 같은 트랜잭션 안에서 저장하고 조회하면 같은 PersistentContext 에 잡히기 때문에,
    // 식별자(id 값)이 같으면 같은 엔티티로 인식한다. (1차 cache)
    Assertions.assertEquals(findMember, member);
  }
}
