
package tacos.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import tacos.TacoOrder;

/*
 * This service sends TacoOrder objects to an Apache Kafka topic.
 * KafkaTemplate handles converting and publishing the order to Kafka.
 * The order is published to the tacocloud.orders.topic topic for consumers to process.
 */

@Service
public class KafkaOrderMessagingService
        implements OrderMessagingService {

    private KafkaTemplate<String, TacoOrder> kafkaTemplate;

    // Inject KafkaTemplate
    @Autowired
    public KafkaOrderMessagingService(
            KafkaTemplate<String, TacoOrder> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendOrder(TacoOrder order) {

        // Send order to the Kafka topic
        kafkaTemplate.send("tacocloud.orders.topic", order);
    }
}

