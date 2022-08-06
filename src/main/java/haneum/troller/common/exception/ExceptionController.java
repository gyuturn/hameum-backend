package haneum.troller.common.exception;


import haneum.troller.common.exception.exceptions.JWTException;
import haneum.troller.common.exception.exceptions.KakaoLoginException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionController {

    //param 파라미터 핸들링
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity missParameterHandler(MissingServletRequestParameterException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST)
                .exception(e.getClass().getSimpleName())
                .message(e.getMessage())
                .build();

        log.error("error: {}", errorResponse.toString());
        return new ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST);
    }

    //카카오 로그인 에러 핸들링
    @ExceptionHandler(KakaoLoginException.class)
    public ResponseEntity missParameterHandler(KakaoLoginException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST)
                .exception(e.getClass().getSimpleName())
                .message(e.getMessage())
                .build();

        log.error("error: {}", errorResponse.toString());
        return new ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(ExpiredJwtException.class)
//    public ResponseEntity ExpiredJwt(ExpiredJwtException e) {
//        ErrorResponse errorResponse = ErrorResponse.builder()
//                .statusCode(HttpStatus.FORBIDDEN)
//                .exception(e.getClass().getSimpleName())
//                .message(e.getMessage())
//                .build();
//
//        log.error("error: {}", errorResponse.toString());
//        return new ResponseEntity(errorResponse, HttpStatus.FORBIDDEN);
//    }

    @ExceptionHandler(JWTException.class)
    public ResponseEntity JwtException(JWTException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(HttpStatus.FORBIDDEN)
                .exception(e.getClass().getSimpleName())
                .message(e.getMessage())
                .build();

        log.error("error: {}", errorResponse.toString());
        return new ResponseEntity(errorResponse, errorResponse.getStatusCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity entireException(Exception e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .exception(e.getClass().getSimpleName())
                .message(e.getMessage())
                .build();

        log.error("error: {}", errorResponse.toString());
        return new ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
