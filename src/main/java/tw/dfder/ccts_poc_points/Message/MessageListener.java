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
    private final  ServiceConfig serviceConfig;

    @Autowired
    public MessageListener(Gson gson, CCTSMessageSender sender, PointRepository repo, ServiceConfig serviceConfig) {
        this.gson = gson;
        this.sender = sender;
        this.repo = repo;
        this.serviceConfig = serviceConfig;
    }


    @RabbitListener(queues = {
            RabbitmqConfig.QUEUE_UPDATEPOINT_REQUEST
    })
    public void messageReceiver(String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel ch) throws IOException {
//        decode the message
        UpdatePointsEnvelope updatePointsEnvelope = gson.fromJson(msg, UpdatePointsEnvelope.class);

        ch.basicAck(deliveryTag, false);
        // add invalid test trigger
        if(updatePointsEnvelope.getPoints() < -1000 || updatePointsEnvelope.getPoints() > 1000) {
            // invalid , retrun invaid result
            sendFailResult(updatePointsEnvelope);
            System.out.println("send rollback result");
        }else if (updatePointsEnvelope.getCommunicationType().equals("rollback")){
            compensatingProcess(updatePointsEnvelope);
        }else if (updatePointsEnvelope.getPaymentId() != null
                && updatePointsEnvelope.getBuyerId() != null
                && updatePointsEnvelope.isValid()
                && updatePointsEnvelope.getCommunicationType().equals("request")){
            UpdatePointsEnvelope response = new UpdatePointsEnvelope();
            response.setPoints(updatePointsEnvelope.getPoints() * serviceConfig.pointRatio);
            response.setBuyerId(updatePointsEnvelope.getBuyerId());
            response.setPaymentId(updatePointsEnvelope.getPaymentId());
            response.setCommunicationType("success");
            response.setValid(true);

            sender.sendRequestMessage(
                    gson.toJson(response),
                    "orchestrator",
                    RabbitmqConfig.ROUTING_UPDATEPOINT_RESPONSE,
                    "t-point-orc-01",
                    "4"
            );

            System.out.println("Success!!" + updatePointsEnvelope);
        }

    }

    private void sendFailResult(UpdatePointsEnvelope updatePointsEnvelope) {
        updatePointsEnvelope.setCommunicationType("fail");
        System.out.println("send fail result");
        sender.sendRequestMessage(
                gson.toJson(updatePointsEnvelope),
                "orchestrator",
                RabbitmqConfig.ROUTING_UPDATEPOINT_RESPONSE,
                "t-point-orc-02",
                "9"
        );
    }


    public void compensatingProcess(UpdatePointsEnvelope updatePointsEnvelope){
        System.out.println("In compensating process branch");
        repo.deleteByPaymentId(updatePointsEnvelope.getPaymentId());
        sender.sendRequestMessage(
                gson.toJson(updatePointsEnvelope),
                "orchestrator",
                RabbitmqConfig.ROUTING_UPDATEPOINT_RESPONSE,
                "t-point-orc-03",
                "11"
        );
    }




}
