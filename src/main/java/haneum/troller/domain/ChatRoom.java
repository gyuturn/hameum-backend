package haneum.troller.domain;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import haneum.troller.Enum.LoginType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

    @GeneratedValue
    @Id
    @Column(name = "chatRoom_id")
    private Long chatRoomId;

//    private String opponent;
    private Long opponentId;

    @OneToMany(mappedBy = "chatRoom")
    private List<MemberChat> memberChats = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom")
    @JsonManagedReference
    private List<Message> messages = new ArrayList<>();

    @Builder
    public ChatRoom(Long opponent) {
        this.opponentId = opponent;
    }


}
