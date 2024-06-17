package com.shop.entity;

import com.shop.constant.Role;
import com.shop.dto.MemberFormDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity // 나 엔티티야
@Table(name = "member") //테이블 명
@Getter
@Setter
@ToString
public class Member {
    //기본키 컬럼명 = member_id AI -> 데이터 저장
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  Long id;
    //알아서 설정
    private String name;
    //중복허용 X
    @Column(unique = true)
    //알아서
    private String email;
    //알아서
    private  String password;
    //알아서
    private String address;

    private String tel;
    //Enum -> 컴 : 숫자 우리 : 문자
    //데이터베이스 문자 그대로 -> USER, ADMIN
    @Enumerated(EnumType.STRING)

    private Role role;

    //public static << 나는 유일한 존재다.
    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder){

        Member member= new Member();
        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setAddress(memberFormDto.getAddress());
        String password = passwordEncoder.encode(memberFormDto.getPassword()); //암호화
        member.setPassword(password);
        member.setTel(memberFormDto.getTel());
        member.setRole(Role.ADMIN);
        return member;
    }
}
