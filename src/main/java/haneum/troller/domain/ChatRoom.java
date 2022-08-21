package haneum.troller.domain;


import haneum.troller.Enum.LoginType;
import lombok.AccessLevel;
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

    @OneToMany(mappedBy = "chatRoom")
    private List<MemberChat> memberChats = new ArrayList<>();
}
