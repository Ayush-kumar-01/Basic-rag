package memory_app.controllers;

import memory_app.service.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/chat")
    public ResponseEntity<String> chat(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "query", required = false) String query,
            @RequestHeader(value = "userId", required = false, defaultValue = "default-user") String userId
    ) {
        String prompt = resolveQuery(q, query);
        if (prompt == null || prompt.isBlank()) {
            return ResponseEntity.badRequest().body("Please provide a query parameter '?q=what is java' or '?query=what is java'");
        }
        return ResponseEntity.ok(chatService.chatTemplate(prompt, userId));
    }

    @PostMapping("/chat")
    public ResponseEntity<String> chatPost(
            @RequestBody(required = false) Map<String, String> body,
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "query", required = false) String query,
            @RequestHeader(value = "userId", required = false, defaultValue = "default-user") String userId
    ) {
        String prompt = resolveQuery(q, query);
        if ((prompt == null || prompt.isBlank()) && body != null) {
            prompt = body.getOrDefault("query", body.getOrDefault("q", body.get("message")));
        }
        if (prompt == null || prompt.isBlank()) {
            return ResponseEntity.badRequest().body("Please provide a query via JSON body (e.g. {\"query\": \"what is java\"}) or URL parameter '?q=...'");
        }
        String effectiveUserId = (body != null && body.containsKey("userId")) ? body.get("userId") : userId;
        return ResponseEntity.ok(chatService.chatTemplate(prompt, effectiveUserId));
    }

    @GetMapping("/stream-chat")
    public ResponseEntity<Flux<String>> streamChat(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "query", required = false) String query
    ){
        String prompt = resolveQuery(q, query);
        if (prompt == null || prompt.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(this.chatService.streamChat(prompt));
    }

    @PostMapping("/documents")
    public ResponseEntity<Void> saveDocuments(@RequestBody List<String> documents) {
        if (documents == null || documents.isEmpty() || documents.stream().anyMatch(String::isBlank)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provide a non-empty list of non-blank documents"
            );
        }

        chatService.saveData(documents);
        return ResponseEntity.noContent().build();
    }

    private String resolveQuery(String q, String query) {
        if (q != null && !q.isBlank()) {
            return q;
        }
        if (query != null && !query.isBlank()) {
            return query;
        }
        return null;
    }
}