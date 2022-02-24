package tw.dfder.ccts_poc_points.Message;


import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import tw.dfder.ccts_poc_points.Entity.UpdatePointsEnvelope;
import tw.dfder.ccts_poc_points.configuration.RabbitmqConfig;
import tw.dfder.ccts_poc_points.configuration.ServiceConfig;
import tw.dfder.ccts_poc_points.repository.PointRepository;

import java.io.IOException;

@EnableRabbit
@Service("MessageListener")
public class MessageListener {
    private final Gson gson;
    private final CCTSMessageSender sender;
    private final PointRepository repo;


    @Autowired
    public MessageListener(Gson gson, CCTSMessageSender sender, PointRepository repo) {
        this.gson = gson;
        this.sender = sender;
        this.repo = repo;
    }


    @RabbitListener(queues = {
            RabbitmqConfig.QUEUE_UPDATEPOINT_REQUEST
    })
    public void messageReceiver(String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel ch) throws IOException {
//        decode the message
        UpdatePointsEnvelope updatePointsEnvelope = gson.fromJson(msg, UpdatePointsEnvelope.class);

        ch.basicAck(deliveryTag, false);
        // add invalid test trigger
        if(updatePointsEnvelope.getPoints() < -1000 || updatePointsEnvelope.getPoints() > 1000){
            compensatingProcess(updatePointsEnvelope);
        }else if (updatePointsEnvelope.getPaymentId() != null && updatePointsEnvelope.getBuyerId() != null && updatePointsEnvelope.isValid()){
            updatePointsEnvelope.setValid(true);
            repo.save(updatePointsEnvelope);
            sender.sendRequestMessage(
                    gson.toJson(updatePointsEnvelope),
                    "orchestrator",
                    RabbitmqConfig.ROUTING_UPDATEPOINT_RESPONSE,
                    ServiceConfig.serviceName
            );

            System.out.println("Success!!" + updatePointsEnvelope);
        }

    }

    public void compensatingProcess(UpdatePointsEnvelope updatePointsEnvelope){
        // TODO:
        System.out.println("In compensating process branch");
    }




}
