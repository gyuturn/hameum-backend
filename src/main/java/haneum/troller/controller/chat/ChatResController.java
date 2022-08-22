package haneum.troller.controller.chat;

import haneum.troller.common.aop.annotation.Auth;
import haneum.troller.domain.ChatRoom;
import haneum.troller.domain.Member;
import haneum.troller.domain.MemberChat;
import haneum.troller.dto.chat.ChatRoomDto;
import haneum.troller.repository.ChatRoomRepository;
import haneum.troller.repository.MemberChatRepository;
import haneum.troller.repository.MemberRepository;
import haneum.troller.service.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@Tag(name="채팅 서비스",description = "채팅서비스 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
@Slf4j
public class ChatResController {
    private final ChatRoomRepository chatRoomRepository;
    private final JwtService jwtService;
    private final MemberChatRepository memberChatRepository;
    private final MemberRepository memberRepository;


    @Operation(summary = "채팅방 생성 api", description = "채팅방 생성을 위한 api")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",description = "정상적 조회"),
                    @ApiResponse(responseCode = "404",description = "해당 유저가 없음")
            }
    )
    @PostMapping("/room")
    @Auth
    public ResponseEntity createChatroom(@RequestHeader("JWT-accessToken") String accessToken,@RequestParam("opponent")String lolName){
        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.builder()
                .opponent(lolName)
                .build());

        Long memberId = Long.valueOf(jwtService.getSubjectByToken(accessToken));
        Member member1 = memberRepository.findById((memberId)).get();
        Member member2 = memberRepository.findByLolName(lolName).get(0);

        memberChatRepository.save(MemberChat.builder()
                .member(member1)
                .chatRoom(chatRoom)
                .build());
        memberChatRepository.save(MemberChat.builder()
                .member(member2)
                .chatRoom(chatRoom)
                .build());


        ChatRoomDto chatRoomDto = ChatRoomDto.builder()
                .ChatRoomId(chatRoom.getChatRoomId())
                .OpponentLolName(lolName)
                .build();
        return new ResponseEntity(chatRoomDto, HttpStatus.OK);
    }

    @Operation(summary = "채팅방 조회 api", description = "자신과 연결되어 있는 채팅방을 조회함.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "정상적 조회"),
            }
    )
    @GetMapping("/rooms/all")
    public ResponseEntity selectAllChatRoom(@RequestHeader("JWT-accessToken") String accessToken) {
        Long memberId = Long.valueOf(jwtService.getSubjectByToken(accessToken));
        Member member = memberRepository.findById((memberId)).get();
        List<MemberChat> memberChatList = memberChatRepository.findByMember(member);

        List<ChatRoomDto> chatRoomDtoList = new ArrayList<>();
        for (MemberChat memberChat : memberChatList) {
            chatRoomDtoList.add(ChatRoomDto.builder()
                    .ChatRoomId(memberChat.getChatRoom().getChatRoomId())
                    .OpponentLolName(memberChat.getChatRoom().getOpponent())
                    .build());
        }

        return new ResponseEntity(chatRoomDtoList, HttpStatus.OK);
    }
}
