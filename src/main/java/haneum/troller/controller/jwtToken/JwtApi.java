package haneum.troller.controller.jwtToken;


import haneum.troller.dto.jwtDto.JwtDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "JWT Token API", description = "JWT TOKEN 관련 API")
@RequestMapping("/token")
@RestController
@RequiredArgsConstructor
public class JwtApi {


//    @Operation(summary = "access,refresh token 발급", description = "로그인 성공시 해당 api 사용" +
//            "200인 경우 정상적인 로그인 <-> " +
//            "401인 경우 비정상적인 로그인(로그인 실패)")
//    @Parameters(
//            {
//                    @Parameter(name = "httpStatusCode", description = "http status code"),
//            }
//    )
//    @GetMapping("/serve/tokens")
//    public JwtDto serveBothTokens(@RequestParam("httpStatusCode") String code) {
//        if (code == "200") {
//
//        }else{
//
//        }
//    }
}
