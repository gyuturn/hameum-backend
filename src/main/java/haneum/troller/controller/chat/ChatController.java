package haneum.troller.controller.chat;


import haneum.troller.domain.ChatRoom;
import haneum.troller.domain.Member;
import haneum.troller.domain.Message;
import haneum.troller.dto.chat.MessageDto;
import haneum.troller.dto.chat.MessageReturnDto;
import haneum.troller.repository.ChatRoomRepository;
import haneum.troller.repository.MemberRepository;
import haneum.troller.repository.MessageRepository;
import haneum.troller.service.security.JwtService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
@Slf4j
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    //app/chat/message
    @MessageMapping("/chat/message")
    public void message(MessageDto messageDto) {
        log.info("채팅메세지보냄");
        ChatRoom chatRoom = chatRoomRepository.findById(messageDto.getChatRoomId()).get();

        String accessToken = messageDto.getAccessToken();
        Member member = memberRepository.findById(Long.valueOf(Long.valueOf(jwtService.getSubjectByToken(accessToken)))).get();
        String lolName = member.getLolName();

        Message message = Message.builder()
                .chatRoom(chatRoom)
                .content(messageDto.getContent())
                .sender(lolName)
                .build();
        messageRepository.save(message);

        MessageReturnDto messageReturnDto = MessageReturnDto.builder()
                .chatRoomId(message.getChatRoom().getChatRoomId())
                .createDate(message.getCreateDate())
                .content(message.getContent())
                .sender(message.getSender())
                .build();
        log.info("메세지 보냄");
        log.info("messageDto: {}", messageReturnDto);

        messagingTemplate.convertAndSend("/topic/chat_room/" + messageDto.getChatRoomId(), messageReturnDto);
    }
}
