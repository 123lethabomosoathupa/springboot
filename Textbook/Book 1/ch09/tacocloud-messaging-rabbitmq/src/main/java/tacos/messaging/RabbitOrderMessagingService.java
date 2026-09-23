
package tacos.messaging;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tacos.TacoOrder;

/*
 * This service sends TacoOrder objects to a RabbitMQ message queue.
 * RabbitTemplate converts the order into a message and sends it to the queue.
 * A custom header is added to identify that the order originated from the web application.
 */

@Service
public class RabbitOrderMessagingService
        implements OrderMessagingService {

    private RabbitTemplate rabbit;

    // Inject RabbitTemplate
    @Autowired
    public RabbitOrderMessagingService(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    @Override
    public void sendOrder(TacoOrder order) {

        // Send order to the RabbitMQ queue
        rabbit.convertAndSend("tacocloud.order.queue", order,
            new MessagePostProcessor() {

                @Override
                public Message postProcessMessage(Message message)
                        throws AmqpException {

                    // Add order source information
                    MessageProperties props =
                            message.getMessageProperties();

                    props.setHeader("X_ORDER_SOURCE", "WEB");

                    return message;
                }
            });
    }
}

