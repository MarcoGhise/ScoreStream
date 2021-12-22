package it.blog.score.stream;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.rabbitmq.stream.Consumer;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.OffsetSpecification;

import it.blog.score.bean.Movie;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Profile("consumer")
public class StreamConsumer implements ApplicationRunner {

	private final static String stream = "StreamScore";
	
	@Override
	public void run(ApplicationArguments args) throws Exception {

		log.info("Connecting...");
		// Create an environment that will connect to localhost:5552
		Environment environment = Environment.builder().build();

		List<Movie> movies = new ArrayList<>();

		// Use Environment#consumerBuilder() to define the consumer
		Consumer consumer = environment.consumerBuilder()
				// Start consuming from the beginning of the stream
				.stream(stream)
				.name("consumer-1")
				.autoTrackingStrategy() 
				.builder()
				.offset(OffsetSpecification.first())
				// Set up the logic to handle messages
				.messageHandler((offset, message) -> {
					
					// Processing time
					long timeStampNow = Instant.now().toEpochMilli();
					// Parse message
					Movie review = this.deserializeScore(message.getBodyAsBinary());
					// Add to collection
					movies.add(review);					
					
					log.info("Total review: {}", movies.size());
					// Calc and show rating
					movies.stream().filter(m -> m.getTimeStamp() > timeStampNow - 60000).collect(Collectors.groupingBy(Movie::getTitle)).forEach(this::showRatings);		
					
					// Remove old items
					movies.removeIf(m -> m.getTimeStamp() < timeStampNow - 60000);

				}).build();
	}
	
	private void showRatings(String key , List<Movie> movie)
	{
		double rating = movie.stream().map(Movie::getStars).reduce(Integer::sum).map(r -> Double.valueOf(r) /movie.size()).get();
		log.info("Rating for {} is {} on {} reviews", key, rating, movie.size());
	}
	
	public Movie deserializeScore(byte[] data) {
		String s = new String(data);
		return new Movie(s.split(",")[0], Integer.valueOf(s.split(",")[1]),
				Long.valueOf(s.split(",")[2]));
	}
}
