package jpabook.jpashop.controller;

import java.util.List;
import javax.validation.Valid;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;

  @GetMapping("/members/new")
  public String createForm(Model model) {
    model.addAttribute("memberForm", new MemberForm());
    return "members/createMemberForm";
  }

  @PostMapping("/members/new")
  public String create(@Valid MemberForm form, BindingResult result) {
    // 왜 Member Entity 를 직접 사용하지 않는가?
    // 실무에서의 요구사항은 엔티티와 화면이 1:1 매칭되는 경우가 거의없음!
    // 화면에서 받아오는 필드/validation 과 도메인의 필드/validation 이 다르고
    // -> 하나의 클래스로 관리할 경우 설정해야 할 항목들이 너무 많고,
    // 엔티티가 화면 종속적으로 계속 추가되면서 지저분해짐
    // 엔티티는 순수하게 핵심 비즈니스 로직만 가지고 있도록 유지, 화면에 대한건 Form 객체나 DTO 를 사용!

    // error 가 발생할 경우 서버 사이드 렌더링 하며 valid error message 와 함께 return form page
    if (result.hasErrors()) {
      return "members/createMemberForm";
    }

    Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());
    Member member = new Member();
    member.setName(form.getName());
    member.setAddress(address);

    memberService.join(member);

    return "redirect:/";
  }

  @GetMapping("/members")
  public String list(Model model) {
    // DTO 로 변환하여 화면에 보여지는 필드들로만 정제하여 반환할 것을 권장. (여기서는 간단한 로직이라 그냥 진행하였음..)

    // [중요] API 를 만들 때에는 **절대로** 엔티티를 외부로 반환하여서는 안됨.
    // 1. password 와 같은 중요 필드가 노출되는 위험성 발생
    // 2. 엔티티 == 스펙. 이므로 엔티티에 로직이 추가될 경우 API 의 스펙이 변화되어버림
    List<Member> members = memberService.findMembers();
    model.addAttribute("members", members);
    return "/members/memberList";
  }
}
