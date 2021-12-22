package it.blog.score.serde;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import it.blog.score.bean.Score;

public class ScoreSerde implements Serde<Score> {

	@Override
	public Serializer<Score> serializer() {
		return new Serializer<Score>() {
			@Override
			public byte[] serialize(String topic, Score data) {
				String s = data.getTitle() + "," + data.getCount() + "," + data.getSum() + "," + data.getValue();
				return s.getBytes();
			}
		};
	}

	@Override
	public Deserializer<Score> deserializer() {
		return new Deserializer<Score>() {

			@Override
			public Score deserialize(String topic, byte[] data) {
				
				String s = new String(data);
				return new Score(s.split(",")[0], Double.valueOf(s.split(",")[1]),
						Double.valueOf(s.split(",")[2]), Double.valueOf(s.split(",")[3]));
						
			}
		};
	}

}