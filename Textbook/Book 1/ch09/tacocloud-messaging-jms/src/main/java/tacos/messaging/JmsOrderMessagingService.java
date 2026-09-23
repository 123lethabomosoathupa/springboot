
package tacos.messaging;

import javax.jms.JMSException;
import javax.jms.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import tacos.TacoOrder;

/*
 * This service sends TacoOrder objects to a JMS message queue.
 * JmsTemplate converts the order into a JMS message and sends it to the queue.
 * A custom property is added to identify the order source as the web application.
 */

@Service
public class JmsOrderMessagingService implements OrderMessagingService {

    private JmsTemplate jms;

    // Inject JmsTemplate
    @Autowired
    public JmsOrderMessagingService(JmsTemplate jms) {
        this.jms = jms;
    }

    @Override
    public void sendOrder(TacoOrder order) {

        // Send order to the JMS queue
        jms.convertAndSend("tacocloud.order.queue", order,
                this::addOrderSource);
    }

    // Add order source information
    private Message addOrderSource(Message message) throws JMSException {

        message.setStringProperty("X_ORDER_SOURCE", "WEB");

        return message;
    }
}

