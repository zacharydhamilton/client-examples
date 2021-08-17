package clients.producers;

import clients.avro.ColorAvro;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
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
import java.util.Random;
import java.util.concurrent.Future;

public class ColorProducerExample {
    public static void main(String[] args) throws IOException {
        Properties props = new Properties();
        addPropsFromFile(props, "setup.properties");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "Clients.avro-color-producer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        KafkaProducer<String, ColorAvro> producer = new KafkaProducer<>(props);

        String[] colors = new String[]{"white", "black", "blue", "orange", "red", "green", "yellow", "purple"};

        for (int i=0; i<10; i++) {
            ColorAvro color = ColorAvro.newBuilder()
                    .setName(colors[new Random().nextInt(colors.length)])
                    .setBrightness(new Random().nextInt(100))
                    .build();
            ProducerRecord<String, ColorAvro> record = new ProducerRecord<String, ColorAvro>("colors", color.getName().toString(), color);
            try {
                Future<RecordMetadata> metadataFuture = producer.send(record);
                System.out.printf("Message with key '%s' sent to topic '%s' partition '%s' with value '%s'.\n",
                        record.key(),
                        metadataFuture.get().topic(),
                        metadataFuture.get().partition(),
                        color.toString());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }



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
