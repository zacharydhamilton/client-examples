package clients.producers;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.Future;

public class ProducerConfigExample {
    public static void main(String[] args) throws IOException {
        Properties props = new Properties();
        addPropsFromFile(props, "setup.properties");

        // Naming
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "example-producer");

        // Serialization
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // Important for consistency
        props.put(ProducerConfig.ACKS_CONFIG, "all");                       // default: 1,     valid: "0", "1", "all", "-1"
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);          // default: false, valid: boolean
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5); // default: 5,     valid: [1,...]

        // Important for performance
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);        // default: 16384, valid: [0,...]
        props.put(ProducerConfig.LINGER_MS_CONFIG, 0);             // default: 0,     valid: [0,...]
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "none"); // default: none,  valid: "gzip", "snappy", "zstd", "lz4", "none"

        // Important for retries
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);  // default: 2147483647, valid: [0,...,2147483647]
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 120000); // default: 120000,     valid: [0,...]

        // Create the KafkaProducer
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        // Create a ProducerRecord
        ProducerRecord<String, String> record = new ProducerRecord<>("colors-schemaless", "white", "the color of the shoes i'm wearing");

        // Send the record, store callback in a Future
        try {
            Future<RecordMetadata> metadataFuture = producer.send(record);
            System.out.printf("Message with key '%s' sent to topic '%s' partition '%s' with future\n",
                    record.key(),
                    metadataFuture.get().topic(),
                    metadataFuture.get().partition());
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        // Send the record, implement callback with lambda
        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                exception.printStackTrace();
            } else {
                System.out.printf("Message with key '%s' sent to topic '%s' partition '%s' with lambda\n",
                        record.key(),
                        metadata.topic(),
                        metadata.partition());
            }
        });

        // Producer shutdown!
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting the producer down.");
            producer.close();
        }));
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
