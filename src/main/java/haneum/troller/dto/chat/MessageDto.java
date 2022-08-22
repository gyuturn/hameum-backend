package haneum.troller.dto.chat;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema
public class MessageDto {
    private Long chatRoomId;

    private String sender;

    private String content;



}
