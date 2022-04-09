package jpabook.jpashop.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.persistence.EntityManager;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {
  // 영속성이 주입될 때 (X) transaction 이 commit 될 때 (O) insert query & commit !

  @Autowired
  MemberService memberService;

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  EntityManager em;

  @Test
//  @Rollback(value = false)
  public void 회원가입() throws Exception {
    // given
    Member member = new Member();
    member.setName("kim");

    // when
    Long saveId = memberService.join(member);

    // then
    // 기본적으로 테스트가 끝나면 spring transactional 이 rollback 시킴.
    // 영속성 내용이 쿼리로 나가고 db에 등록되는지 보고싶다면
    // test method 에 @Rollback(false) or em.flush()를 추가.
    em.flush();
    assertEquals(member, memberRepository.findOne(saveId));
  }

  @Test(expected = IllegalStateException.class)
  public void 중복_회원_예외() throws Exception {
    // given
    Member member1 = new Member();
    member1.setName("lee");

    Member member2 = new Member();
    member2.setName("lee");

    // when
    memberService.join(member1);
    memberService.join(member2); // 예외가 발생하여야 한다 !

    // then
    // 테스트 코드를 잘못 작성했을 때를 대비. 여기로 들어오면 fail 임
    fail("예외 발생");
  }

}