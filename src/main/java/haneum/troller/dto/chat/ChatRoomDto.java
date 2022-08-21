package haneum.troller.dto.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema
@Builder
public class ChatRoomDto {
    private Long ChatRoomId;
    private String OpponentLolName;
}
