package haneum.troller.common.exception.exceptions;

public class JWTException extends Exception{

    public JWTException(String message) {
        super(message); // RuntimeException 클래스의 생성자를 호출합니다.
    }

    public JWTException() {

    }
}