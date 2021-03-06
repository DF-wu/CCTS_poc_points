package tw.dfder.ccts_poc_points.Message;


import com.google.gson.Gson;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.dfder.ccts_poc_points.configuration.RabbitmqConfig;
import tw.dfder.ccts_poc_points.configuration.ServiceConfig;


@EnableRabbit
@Service("CCTSMessageSender")
public class CCTSMessageSender {
    private final RabbitTemplate rabbitTemplate;
    private final ServiceConfig serviceConfig;

    @Autowired
    public CCTSMessageSender(RabbitTemplate rabbitTemplate, Gson gson, ServiceConfig serviceConfig) {
        this.rabbitTemplate = rabbitTemplate;
        this.serviceConfig = serviceConfig;
    }


    public boolean sendRequestMessage(String message, String destination, String routingKey, String testCaseId, String timeSequenceLabel){
//        routingKey : routing key defined in RabbitmqConfig.java
//        destination is the corresponding service name
//        pactName is what the contract of the message belonging for

        try {
            rabbitTemplate.convertAndSend(
                    RabbitmqConfig.EXCHANG_ORCHESTRATOR,
                    routingKey,
                    message,
                    m -> {
                        m.getMessageProperties().getHeaders().put("provider", serviceConfig.name);
                        m.getMessageProperties().getHeaders().put("consumer", destination );
                        m.getMessageProperties().getHeaders().put("testCaseId", testCaseId);
                        m.getMessageProperties().getHeaders().put("timeSequenceLabel", timeSequenceLabel);
                        m.getMessageProperties().getHeaders().put("CCTSTimestamp", System.currentTimeMillis());
                        return m;
                    }
            );
            return true;
        } catch (Exception e){
            System.out.println("ERROR : sendRequestMessage. " + e);
            e.printStackTrace();
            return false;
        }
    }

}
