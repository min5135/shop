package com.shop.service;

import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor // final, @NonNull 변수에 붙으면 자동 주입(@Autowired)을 해준다.
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository; //자동주입 된것.
    public Member saveMember(Member member){
        validateDuplicateMember(member);
        return memberRepository.save(member);// 데이터베이스에 저장을 하라는 명령
    }
    private void validateDuplicateMember(Member member){
        Member findMember = memberRepository.findByEmail(member.getEmail());
        if(findMember != null){//회원가입 할 때 있는지 없는지 하는 작업
            throw new IllegalStateException("이미 가입된 이메일입니다."); // 예외 발생
        }
        findMember = memberRepository.findByTel(member.getTel());
        if(findMember != null){//회원가입 할 때 있는지 없는지 하는 작업
            throw new IllegalStateException("이미 가입된 전화번호입니다."); // 예외 발생
        }

    }
        @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email);
        if(member == null){
            throw new UsernameNotFoundException(email);
        }
        return User.builder().username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString()).build();
    }
}
