package it.blog.score.stream;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.rabbitmq.stream.ByteCapacity;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.Message;
import com.rabbitmq.stream.Producer;
import com.rabbitmq.stream.StreamException;

import it.blog.score.bean.Movie;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Profile("producer")
public class StreamProducer implements ApplicationRunner {

	private final static String stream = "StreamScore";
	
	String[] movieTitle = new String[] { "The Shawshank Redemption", "The Godfather", "The Dark Knight", "12 Angry Men",
	"Schindler's List" };

	@Override
	public void run(ApplicationArguments args) throws Exception {

		log.info("Connecting...");

		// Use Environment#builder to create the environment
		Environment environment = Environment.builder().build(); // <1>

		// Delete existing Stream
		try {
			environment.deleteStream(stream);
		} catch (StreamException se) {
			log.warn("Stream not found");
		}
		/*
		 * Set the maximum size to 10 MB Set the segment size to 1 MB
		 */
		environment.streamCreator().stream(stream).maxLengthBytes(ByteCapacity.MB(10))
				.maxSegmentSizeBytes(ByteCapacity.MB(1)).create();

		log.info("Starting publishing...");

		// Create the Producer with Environment#producerBuilder
		Producer producer = environment.producerBuilder().stream(stream).build();

		Runnable runnable = () -> {

			int scoreValue = new Random().nextInt(10);
			int rndTitle = new Random().nextInt(5);
			
			// Send messages with Producer#send(Message, ConfirmationHandler)
			producer.send(					
					// Create a message with Producer#messageBuilder
					this.buildMovieMessage(producer, scoreValue, rndTitle),
					// Message publishing confirmation
					confirmationStatus -> log.info("Confirmed:{}", confirmationStatus.isConfirmed()));
		};
		// Run the process every second
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(runnable, 1, 2, TimeUnit.SECONDS);
	}
	
	private Message buildMovieMessage(Producer producer, int scoreValue, int rndTitle)
	{
		Movie movie = new Movie(movieTitle[rndTitle], scoreValue);
		
		log.info("Send review:{}", movie);
		
		return producer.messageBuilder().addData(this.serializeScore(movie)) // <3>
		.build();
	}
	
	private byte[] serializeScore(Movie movie) {
		String s = movie.getTitle() + "," + movie.getStars() +"," + movie.getTimeStamp();
		return s.getBytes();
	}
}
