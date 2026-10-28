package memory_app;

import memory_app.helper.Helper;
import memory_app.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class DemoApplication {

	private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner dataLoader(ChatService chatService, JdbcTemplate jdbcTemplate) {
		return args -> {
			try {
				Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM vector_store", Integer.class);
				if (count != null && count > 0) {
					log.info("Vector store already contains {} records. Skipping initial data loading.", count);
					return;
				}
			} catch (Exception e) {
				log.info("Checking vector_store table: {}. Proceeding to load data.", e.getMessage());
			}

			log.info("Starting initial data loading from Helper into MariaDB vector store...");
			chatService.saveData(Helper.getData());
			log.info("Initial data successfully embedded and saved to MariaDB vector store!");
		};
	}
}
