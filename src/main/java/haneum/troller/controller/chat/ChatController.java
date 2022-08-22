package haneum.troller.controller.chat;


import haneum.troller.domain.ChatRoom;
import haneum.troller.domain.Message;
import haneum.troller.dto.chat.MessageDto;
import haneum.troller.dto.chat.MessageReturnDto;
import haneum.troller.repository.ChatRoomRepository;
import haneum.troller.repository.MessageRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;

    //app/chat/message
    @MessageMapping("/chat/message")
    public void message(MessageDto messageDto) {
        ChatRoom chatRoom = chatRoomRepository.findById(messageDto.getChatRoomId()).get();

        Message message = Message.builder()
                .chatRoom(chatRoom)
                .content(messageDto.getContent())
                .sender(messageDto.getSender())
                .build();
        messageRepository.save(message);

        MessageReturnDto messageReturnDto = MessageReturnDto.builder()
                .chatRoomId(message.getChatRoom().getChatRoomId())
                .createDate(message.getCreateDate())
                .content(message.getContent())
                .sender(message.getSender())
                .build();

        messagingTemplate.convertAndSend("/topic/chat_room/" + messageDto.getChatRoomId(), messageReturnDto);
    }
}
