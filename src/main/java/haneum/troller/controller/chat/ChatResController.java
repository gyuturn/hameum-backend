package haneum.troller.controller.chat;

import haneum.troller.common.aop.annotation.Auth;
import haneum.troller.domain.ChatRoom;
import haneum.troller.domain.Member;
import haneum.troller.domain.MemberChat;
import haneum.troller.domain.Message;
import haneum.troller.dto.chat.ChatRoomDto;
import haneum.troller.repository.ChatRoomRepository;
import haneum.troller.repository.MemberChatRepository;
import haneum.troller.repository.MemberRepository;
import haneum.troller.repository.MessageRepository;
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
    private final MessageRepository messageRepository;

    @Operation(summary = "채팅방 생성 api", description = "채팅방 생성을 위한 api")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "이전에 만든방이 존재하였기에 이전의 방 조회"),
                    @ApiResponse(responseCode = "201", description = "새로운 방 생성"),
                    @ApiResponse(responseCode = "404", description = "해당 유저가 없음")
            }
    )
    @PostMapping("/room")
    @Auth
    public ResponseEntity createChatroom(@RequestHeader("JWT-accessToken") String accessToken, @RequestParam("opponent") String lolName) {
        Long memberId = Long.valueOf(jwtService.getSubjectByToken(accessToken));
        Member ourSelfMember = memberRepository.findById((memberId)).get();
        Member opponentMember = memberRepository.findByLolName(lolName).get(0);

        //채팅방 존재하는지 조회
        /**
         1.accesstoken으로 채팅방 조회
         2.그 중 lolname과 일치하는방이 있는지 조회
         */
        List<MemberChat> memberChat = memberChatRepository.findByMember(ourSelfMember);
        for (MemberChat mchat : memberChat) {
            ChatRoom ExistingChatRoom = chatRoomRepository.findById(mchat.getChatRoom().getChatRoomId()).get();
            if (ExistingChatRoom.getOpponentId() == opponentMember.getMemberId()) {
                ChatRoomDto chatRoomDto = ChatRoomDto.builder()
                        .ChatRoomId(ExistingChatRoom.getChatRoomId())
                        .OpponentLolName(opponentMember.getLolName())
                        .build();
                return new ResponseEntity(chatRoomDto, HttpStatus.OK);
            }

        }


        //채팅방 생성
        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.builder()
                .opponent(opponentMember.getMemberId())
                .build());


        memberChatRepository.save(MemberChat.builder()
                .member(ourSelfMember)
                .chatRoom(chatRoom)
                .build());
        memberChatRepository.save(MemberChat.builder()
                .member(opponentMember)
                .chatRoom(chatRoom)
                .build());


        ChatRoomDto chatRoomDto = ChatRoomDto.builder()
                .ChatRoomId(chatRoom.getChatRoomId())
                .OpponentLolName(lolName)
                .build();
        return new ResponseEntity(chatRoomDto, HttpStatus.CREATED);
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

            Member memberOpponent = memberRepository.findById(memberChat.getChatRoom().getOpponentId()).get();

            chatRoomDtoList.add(ChatRoomDto.builder()
                    .ChatRoomId(memberChat.getChatRoom().getChatRoomId())
                    .OpponentLolName(memberOpponent.getLolName())
                    .build());
        }

        return new ResponseEntity(chatRoomDtoList, HttpStatus.OK);
    }

    @Operation(summary = "채팅방 이전 메시지 조회 api", description = "채팅방에 보관된 메시지를 return")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "정상적 조회"),
            }
    )
    @GetMapping("/rooms/messages/{roomId}")
    public ResponseEntity selectAllChatRoom(@PathVariable Long roomId){
        List<Message> messages = chatRoomRepository.findById(roomId).get().getMessages();
        return new ResponseEntity(messages, HttpStatus.OK);
    }
}
