package memory_app;

import memory_app.helper.Helper;
import memory_app.service.ChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FirstProjectApplicationTests {
    @Autowired
    private ChatService chatService;

    @Test
    void saveDataToVectorDatabase() {
        System.out.println("saving data to database");
        this.chatService.saveData(Helper.getData());
        System.out.println("data is saved successfully");
    }

}