package eligibality;

import listeners.EligibilityCheckListener;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class EligibilityCheckerApp {
    public static void main(String[] args) throws NamingException, InterruptedException {
        InitialContext initialContext = new InitialContext();
        Queue requestQueue = (Queue) initialContext.lookup("queue/requestQueue");

        try (ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
             JMSContext context = connectionFactory.createContext()) {

            /*
            Это приложение запускаем первым, а потом ClinicalApp
            Ждем сообщение от ClinicalApp
             */

            JMSConsumer consumer1 = context.createConsumer(requestQueue);
            JMSConsumer consumer2 = context.createConsumer(requestQueue); //для распределения нагрузки
            // consumer.setMessageListener(new EligibilityCheckListener()); комментируем для проверки распределения нагрузки

            for (int i = 0; i < 5; i++) {
                System.out.println("Consumer 1 " + consumer1.receive());
                System.out.println("Consumer 2 " +consumer2.receive());
            }
            
            
            /*
            Нужно только для локального запуска,  чтобы программа не завершилась мгновенно
             */
            //Thread.sleep(10000);
        }
    }
}
