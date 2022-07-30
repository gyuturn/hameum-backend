package haneum.troller.service.login;

import haneum.troller.Enum.LoginType;
import haneum.troller.domain.Member;
import haneum.troller.dto.login.KakaoLoginDto;
import haneum.troller.dto.login.SignInDto;
import haneum.troller.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final KaKaoLoginService kaKaoLoginService;

    //회원가입
    public Long join(Member member) {
        memberRepository.save(member);
        return member.getMemberId();
    }

    //카카오 회원가입
    public Long kakaoJoin(KakaoLoginDto kakaoLoginDto) {
        String email = kaKaoLoginService.createKakaoUser(kakaoLoginDto.getAccessToken());
        Member member = Member.builder()
                .email(email)
                .lolName(kakaoLoginDto.getLolName())
                .build();
        member.updateLoginType(LoginType.KAKAO.label());
        join(member);
        return member.getMemberId();
    }

    //로그인시 소셜로그인으로 가입된 이메일 구분
    public boolean findLoginType(Member member) {
        if (member.getType() == LoginType.KAKAO.label()) {
            return false;
        } else return true;
    }




    //refresh-token 저장
    @Transactional(readOnly = false)
    public Member updateRefreshToken(Member member, String token) {
        member.updateRefreshToken(token);
        return member;
    }



    //로그인(비밀번호)
    public boolean validLogin(SignInDto loginDto) {
        if (passwordEncoder.matches(loginDto.getPassword(), loginDto.getPassword())) {
            return true;
        } else {
            return false;
        }
    }

    //이메일 중복인증
    public boolean checkDuplicateEmail(String email){

        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            return true;
        }
        else return false;

    }

    //롤 닉네임이 이미 사용중인지 체크
    public boolean checkDuplicateLolName(String lolName) {
        List<Member> memberList = memberRepository.findByLolName(lolName);
        if (memberList.size() == 0) {
            return true;
        }
        else return false;
    }
}
