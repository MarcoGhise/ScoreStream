package it.blog.score.stream;

import java.time.Duration;
import java.util.function.Function;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Initializer;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

import it.blog.score.bean.Movie;
import it.blog.score.bean.Score;
import it.blog.score.serde.ScoreSerde;

@Component
@Profile("consumer")
public class StreamConsumer {

	@Bean
	public Function<KStream<Object, Movie>, KStream<String, String>> avgscore() {
		
		return input -> {
			KStream<String, String> output = input.map((key, value) -> new KeyValue<>(value.getTitle(), value))
					.groupByKey(Grouped.with(Serdes.String(), new JsonSerde<>(Movie.class)))
					.windowedBy(TimeWindows.of(Duration.ofSeconds(180)))
					.aggregate(this.initializer(), this.aggregator(), Materialized.with(Serdes.String(), new ScoreSerde()))
					.toStream()
					.map((key, value) -> new KeyValue<>(key.key(), String.valueOf(value.getSum() / value.getCount())));

			output.print(Printed.toSysOut());

			return output;
		};

	}

	public Initializer<Score> initializer() {
		return new Initializer<Score>() {
			@Override
			public Score apply() {
				return new Score();
			}
		};

	}

	public Aggregator<String, Movie, Score> aggregator() {
		return new Aggregator<String, Movie, Score>() {
			@Override
			public Score apply(String k, Movie v, Score s) {
				s.setTitle(k);
				s.setCount(s.getCount() + 1);
				s.setSum(s.getSum() + Integer.valueOf(v.getStars()));
				return s;
			}
		};
	}

}
