package pt.IPLeiria.event_bingo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class EventBingoApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventBingoApplication.class, args);
	}

}
