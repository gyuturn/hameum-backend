package haneum.troller.domain;


import com.fasterxml.jackson.annotation.JsonBackReference;
import haneum.troller.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseTimeEntity {
    @GeneratedValue
    @Id
    @Column(name = "message_id")
    private Long messageId;


    private String sender;

    private String content;

    @CreatedDate
    private String createDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatRoom_id")
    @JsonBackReference
    private ChatRoom chatRoom;

    @Builder
    public Message(ChatRoom chatRoom, String sender, String content) {
        this.chatRoom = chatRoom;
        this.sender = sender;
        this.content = content;
    }
}
