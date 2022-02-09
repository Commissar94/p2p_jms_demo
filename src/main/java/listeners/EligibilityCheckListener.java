package listeners;

import objects.Patient;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class EligibilityCheckListener implements MessageListener {

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage) message;
        try (ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
             JMSContext context = connectionFactory.createContext()) {
            /*
            Достаем пациента из сообщения и предварительно создаем мэпу для ответа
             */
            InitialContext initialContext = new InitialContext();
            Queue replyQueue = (Queue) initialContext.lookup("queue/replyQueue");
            MapMessage replyMessage = context.createMapMessage();
            Patient patient = (Patient) objectMessage.getObject();

            /*
            Просто проверяем имя страховой организацию и выводим ответ
             */

            String insuranceProvider = patient.getInsuranceProvider();
            System.out.println("Provider is : " + insuranceProvider);

            /*
            В зависимости от бизнес логики готовим ответное сообщение и отправляем его в очередь ответов
             */
            if (patient.getInsuranceProvider().equals("Reso") || insuranceProvider.equals("Soglasie")) {
                System.out.println("patient copay is: " + patient.getCopay());
                if (patient.getCopay() < 40 && patient.getAmountToBePayed() < 1000) {
                    replyMessage.setBoolean("eligible", true);
                }
            } else {
                replyMessage.setBoolean("eligible", false);
            }

            JMSProducer producer = context.createProducer();
            producer.send(replyQueue, replyMessage);

        } catch (JMSException | NamingException e) {
            e.printStackTrace();
        }
    }
}
