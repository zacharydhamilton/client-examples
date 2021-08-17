package clients.consumers;

import clients.avro.ColorAvro;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ColorConsumerExample {
    public static void main(String[] args) throws IOException {
        Properties props = new Properties();
        addPropsFromFile(props, "setup.properties");

        props.put(ConsumerConfig.GROUP_ID_CONFIG, "color-consumer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        KafkaConsumer<String, ColorAvro> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("colors"));

        // Start the consumer loop
        try {
            while (true) {
                ConsumerRecords<String, ColorAvro> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, ColorAvro> record : records) {
                    System.out.printf("Message with key '%s' read from topic '%s' partition '%s' offset '%s' and value '%s'.\n",
                            record.key(),
                            record.topic(),
                            record.partition(),
                            record.offset(),
                            record.value());
                }
            }
        } finally {
            consumer.close();
        }
    }
    private static void addPropsFromFile(Properties props, String file) throws IOException {
        if (!Files.exists(Paths.get(file))) {
            throw new IOException("Client config file does not exist or could not be found.");
        }
        try (InputStream inputStream = new FileInputStream(file)) {
            props.load(inputStream);
        }
    }
}
