package clinic;

import objects.Patient;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ClinicalsApp {

    public static void main(String[] args) throws NamingException, JMSException {

        InitialContext initialContext = new InitialContext();
        Queue requestQueue = (Queue) initialContext.lookup("queue/requestQueue");
        Queue replyQueue = (Queue) initialContext.lookup("queue/replyQueue");

        try (ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
             JMSContext context = connectionFactory.createContext("clinicaluser","clinicalpass")) {

            /*
            Отправляем сообщение-объект (пациента)
             */
            JMSProducer producer = context.createProducer();

            ObjectMessage objectMessage = context.createObjectMessage();
            Patient patient = new Patient(123, "Bob", "Reso", 30D, 400D);
            objectMessage.setObject(patient);

            for (int i = 0; i < 10; i++) { //цикл для теста на балансировку загрузки
                producer.send(requestQueue,objectMessage);
            }

            /*
            Получаем ответ, имеет ли право пациент на услугу по ДМС
             */

//            JMSConsumer consumer = context.createConsumer(replyQueue);
//            MapMessage replyMessage = (MapMessage) consumer.receive(30000);
//            System.out.println("Patient eligibility: " + replyMessage.getBoolean("eligible"));
        }

    }
}
