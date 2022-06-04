package jpabook.jpashop.api;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberApiController {

  private final MemberService memberService;

  // 엔티티를 파라미터로 받거나, 웹에 노출하여서는 안됨!!!
  // 1. 엔티티 변경 시 API spec 이 변경되는 치명적 문제
  // 2. 웹에 엔티티가 노출될 시 보안 문제 위험성
  // 3. 요청/응답 스펙을 맞추기 위한 부가 어노테이션 (ex @JsonIgnore, @NotEmpty ...) 증가 시 하나의 엔티티로 관리 불가.
  // => API spec 을 위한 DTO 를 별도 생성하여 관리하여야 함. (/api/v2/members)

  @GetMapping("/api/v1/members")
  public List<Member> memberV1() {
    return memberService.findMembers();
  }

  @GetMapping("/api/v2/members")
  public Result memberV2() {
    List<Member> findMembers = memberService.findMembers();
    List<MemberDto> collect = findMembers.stream().map(m -> new MemberDto(m.getName()))
        .collect(Collectors.toList());
    return new Result(collect.size(), collect);
  }

  @GetMapping("/api/v2.1/members")
  public Result memberV2_page(
      @RequestParam(value = "offset", defaultValue = "0") int offset,
      @RequestParam(value = "limit", defaultValue = "100") int limit) {
    PageRequest pageRequest = PageRequest.of(offset, limit);
    Page<Member> findMembers = memberService.findMembers_paging(pageRequest);
    Page<MemberDto> map = findMembers.map(m -> new MemberDto(m.getName()));

    return new Result(map.getTotalPages(), map);
  }

  @Data
  @AllArgsConstructor
  static class Result<T> {

    private int count;
    private T data;

  }

  @Data
  @AllArgsConstructor
  static class MemberDto {

    private String name;

  }

  @PostMapping("/api/v1/members")
  public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
    Long id = memberService.join(member);
    return new CreateMemberResponse(id);
  }

  @PostMapping("/api/v2/members")
  public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
    Member member = new Member();
    member.setName(request.getName());
    Long id = memberService.join(member);
    return new CreateMemberResponse(id);

  }

  // PUT 의 멱등성
  @PutMapping("/api/v2/members/{id}")
  public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id,
      @RequestBody @Valid UpdateMemberRequest request) {

    // update() 에서 return Member 하지 않는 이유?
    // 1. command(변경성 메서드) 와 query(조회성 메서드) 를 철저히 분리하자.
    // 2. return 받은 객체는 영속성 컨텍스트에서 관리되지 않음 -> 관리 포인트 복잡해질 수 있음
    memberService.update(id, request.getName());
    Member findMember = memberService.findOne(id);
    return new UpdateMemberResponse(findMember.getId(), findMember.getName());
  }

  @Data
  static class UpdateMemberRequest {

    private String name;
  }

  @Data
  @AllArgsConstructor
  static class UpdateMemberResponse {

    private Long id;
    private String name;
  }

  @Data
  static class CreateMemberRequest {

    private String name;
  }

  @Data
  static class CreateMemberResponse {

    private Long id;

    public CreateMemberResponse(Long id) {
      this.id = id;
    }
  }
}
