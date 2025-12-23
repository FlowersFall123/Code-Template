package com.example.backend.controller.AI;

/*
 * @Auther:fz
 * @Date:2025/5/30
 * @Description:
 */


import com.example.backend.entity.po.User;
import com.example.backend.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/AI/")
public class AIController {

    @Autowired
    OpenAiChatModel chatModel;

    private ChatClient chatClient;

    @Autowired
    private JdbcChatMemoryRepository jdbcChatMemoryRepository;

    @Autowired
    private VectorStore vectorStore;

    @Resource
    UserService userService;

    public AIController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    //流式输出---在跨域里面需要配置异步操作（实现configureAsyncSupport）、JWT也需要配置asyncSupported = true
    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE )
    public Flux<String> chat2(@RequestParam("message") String message,
                              @RequestParam("userId") Integer userId) {
        //初始化
        Init();
        queryVectorByMessage();


        // 获取历史消息并生成回复
        Flux<String> aiResponseFlux = chatClient
                .prompt(SetPrompt(message,userId))
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, userId.toString()))
                .advisors(new QuestionAnswerAdvisor(vectorStore))
                .stream()
                .content();
        return aiResponseFlux.delayElements(Duration.ofMillis(100));
    }

    /**
     * 初始化，（记忆、数据库里面数据）
     */
    private void Init(){
// 创建聊天记忆实例
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(jdbcChatMemoryRepository)  // 使用注入的自定义repository
                .maxMessages(20)
                .build();

         chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    /**
     * 设置提示词
     * @param message
     * @param userId
     * @return
     */
    private Prompt SetPrompt(String message,Integer userId){
        // 将用户消息转换为UserMessage对象
        Message userMessage = new UserMessage(message);

        // 定义系统提示模板文本，使用{name}和{voice}作为可替换变量
        // 这里定义了AI助手的角色、名称和回复风格
        String systemText = """
                你是一个基于人工智能技术构建的智能助手，专门用于快速、准确地检索和整理信息。
                 - 始终保持礼貌、耐心和专业。
                 - 理解用户意图，必要时主动询问澄清问题，以确保回答精准。
                 -在回答问题时可能需要用户的Id，用户的Id是{id}
                 - 采用"{voice}"指定的风格：
                   - 如果是"幽默风趣"，可适当插入轻松幽默的语句；
                   - 如果是"简洁严谨"，则重点突出核心要点；
                   - 如果是"温暖亲切"，则多使用鼓励和关怀的表达。
                 - 对于复杂问题，分步骤列出解决思路或操作流程。
                 - 提供引用或来源时，标明出处，保证信息可信可查。
                  - 如遇超出能力范围或无法验证的信息，要诚实告知并建议用户进一步咨询可靠渠道。
                """;

        // 创建系统提示模板对象
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemText);

        // 填充模板变量，创建系统消息
        Message systemMessage = systemPromptTemplate.createMessage(Map.of("name", "枫梓的专属AI", "id",userId,"voice", "温暖亲切"));

        // 将用户消息和系统消息组合成一个Prompt对象
        // 系统消息会指导AI如何回复用户消息
        return new Prompt(List.of(userMessage, systemMessage));
    }

    /**
     * 将数据库用户数据写入Redis向量库，用于RAG检索
     */
    private void queryVectorByMessage(){
        //1.从数据库获取所有用户数据
        List<User> userList=userService.getAllUser();

        //2.将用户数据转换为向量文档
        List<Document> documents = userList.stream()
                .map(user -> Document.builder()
                        // 文档唯一标识：用于精确删除、更新、去重
                        .id("user_" + user.getId())
                        // 正文内容：会被向量化，用于相似度检索（用户问问题时，AI会基于这个内容回答）
                        .text(user.getUsername())
                        // 元数据：附加信息，不参与向量化，用于业务分类、来源追踪、权限控制等
                        .metadata(Map.of(
                                "userId", user.getId(),
                                "username", user.getUsername(),
                                "account", user.getAccount()
                        ))
                        .build())
                .toList();

        //3.将文档写入Redis向量库
        vectorStore.add(documents);
    }
}
