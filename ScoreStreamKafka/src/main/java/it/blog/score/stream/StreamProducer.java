package it.blog.score.stream;

import java.util.Random;
import java.util.function.Supplier;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import it.blog.score.bean.Movie;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Profile("producer")
public class StreamProducer {

	String[] movieTitle = new String[] { "The Shawshank Redemption", "The Godfather", "The Dark Knight", "12 Angry Men",
			"Schindler's List" };

	@Bean
	public Supplier<Message<Movie>> moviereview() {

		return () -> {

			int scoreValue = new Random().nextInt(10);
			int rndTitle = new Random().nextInt(5);

			Movie movie = new Movie();
			movie.setTitle(movieTitle[rndTitle]);
			movie.setStars(scoreValue);

			Message<Movie> o = MessageBuilder.withPayload(movie)
					.setHeader(KafkaHeaders.MESSAGE_KEY, movie.getTitle().getBytes()).build();
			log.info("Review: {}", o.getPayload());
			return o;

		};
	}
}