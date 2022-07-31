package haneum.troller.common.exception;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ErrorResponse {
    HttpStatus statusCode;
    String exception;
    String message;
}
