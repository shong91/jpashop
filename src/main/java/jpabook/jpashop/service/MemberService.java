package jpabook.jpashop.service;

import java.util.List;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true) // spring annotation 사용을 권장 (NOT javax). default = false
@RequiredArgsConstructor
public class MemberService {

  // constructor injection
  // MemberRepository 객체를 autowired 로 injection 주입하는 방법보다 개선된 방법
  // 중간에 변경되지 않으며, 테스트 코드 작성에 용이
  // 1-1 직접 생성자 함수 생성 시, 생성자가 1개만 있는 경우, @Autowired annotation 없이 스프링이 자동으로 injection
  // 1-2 @RequiredArgsConstructor 을 사용하면 final field 에 대해 생성자 생성

  private final MemberRepository memberRepository;


  // 회원 가입
  @Transactional
  public Long join(Member member) {
    // 중복 회원 검증
    validateDuplicateMember(member);
    memberRepository.save(member);
    return member.getId();
  }

  public void validateDuplicateMember(Member member) {
    // EXCEPTION
    List<Member> findMembers = memberRepository.findByName(member.getName());
    if (!findMembers.isEmpty()) {
      throw new IllegalStateException("Member is already in use");
    }
  }

  // 회원 전체 조회
  // readOnly = true 설정하여 조회 기능 최적화
  public List<Member> findMembers() {
    return memberRepository.findAll();
  }

  public Member findOne(Long memberId) {
    return memberRepository.findById(memberId).get();
  }

  @Transactional
  public void update(Long id, String name) {
    Member member = memberRepository.findById(id).get();
    member.setName(name);
  }
}
