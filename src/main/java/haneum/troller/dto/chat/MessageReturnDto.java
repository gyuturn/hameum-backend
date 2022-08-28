package haneum.troller.dto.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema
@Builder
public class MessageReturnDto {
    private Long chatRoomId;

    private String sender;

    private String content;
    private String createDate;

    @Override
    public String toString() {
        return "MessageReturnDto{" +
                "chatRoomId=" + chatRoomId +
                ", sender='" + sender + '\'' +
                ", content='" + content + '\'' +
                ", createDate='" + createDate + '\'' +
                '}';
    }
}
