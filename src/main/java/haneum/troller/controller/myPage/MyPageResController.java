package haneum.troller.controller.myPage;

import haneum.troller.dto.member.myPage.MyPageDto;
import haneum.troller.security.SecurityService;
import haneum.troller.service.MyPageService;
import haneum.troller.service.dataDragon.MyPageImgService;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@RestController
@RequiredArgsConstructor
public class MyPageResController {
    private final SecurityService securityService;
    private final MyPageService myPageService;
    private final MyPageImgService myPageImgService;

    @GetMapping("/my_page")
    public MyPageDto getTokenForMyPage(@RequestParam(value = "userName") String userName) throws IllegalAccessException, ParseException, IOException {
        MyPageDto myPageDto = myPageService.getEncryptedLolName(userName); //level,icon,name 저장
        MyPageDto myPageDtoFinal = myPageService.getMyPageAttr(myPageDto);
        return myPageDtoFinal;
    }



    @GetMapping(
            path = "/tier_img", produces = "image/png"
    )
    public void getTierImg(HttpServletResponse response, @RequestParam(value="tier") String tier) throws IOException {
        OutputStream out = response.getOutputStream();
        FileInputStream fis = null;


        try {
            fis = new FileInputStream("../../service/dataDragon");
            FileCopyUtils.copy(fis, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
            out.flush();
        }

    }
}
