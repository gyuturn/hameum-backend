package haneum.troller.common.exception.exceptions;

public class KakaoLoginException extends Exception{

    public KakaoLoginException(String message) {
        super(message); // RuntimeException 클래스의 생성자를 호출합니다.
    }

    public KakaoLoginException() {

    }
}
