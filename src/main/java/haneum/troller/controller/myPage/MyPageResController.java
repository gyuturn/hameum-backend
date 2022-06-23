package haneum.troller.controller.myPage;

import haneum.troller.dto.member.myPage.MyPageDto;
import haneum.troller.security.SecurityService;
import haneum.troller.service.MyPageService;
import haneum.troller.service.dataDragon.MyPageImgService;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class MyPageResController {
    private final SecurityService securityService;
    private final MyPageService myPageService;
    private final MyPageImgService myPageImgService;

    @GetMapping("/my_page")
    public MyPageDto getTokenForMyPage(@RequestParam(value = "userToken") String userToken) throws IllegalAccessException, ParseException, IOException {
        if (!securityService.validToken(userToken)) {
            throw new IllegalAccessException("토큰이 유효하지 않습니다!");
        }
        String lolName = securityService.findLolNameByToken(userToken);
        MyPageDto myPageDto = myPageService.getEncryptedLolName(lolName); //level,icon,name 저장
        MyPageDto myPageDtoFinal = myPageService.getMyPageAttr(myPageDto);


        return myPageDtoFinal;

    }
}
