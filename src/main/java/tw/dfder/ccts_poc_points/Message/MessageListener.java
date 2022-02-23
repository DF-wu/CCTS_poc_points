package tw.dfder.ccts_poc_points.Message;


import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import tw.dfder.ccts_poc_payment.Entity.PaymentMessageEnvelope;
import tw.dfder.ccts_poc_payment.configuration.RabbitmqConfig;
import tw.dfder.ccts_poc_payment.configuration.ServiceConfig;
import tw.dfder.ccts_poc_payment.repository.PaymentRepo;
import tw.dfder.ccts_poc_points.Entity.UpdatePointsEnvelope;
import tw.dfder.ccts_poc_points.configuration.RabbitmqConfig;

import java.io.IOException;

@EnableRabbit
@Service("MessageListener")
public class MessageListener {
    private final Gson gson;
    private final CCTSMessageSender sender;
    private final PaymentRepo repo;


    @Autowired
    public MessageListener(Gson gson, CCTSMessageSender sender, PaymentRepo repo) {
        this.gson = gson;
        this.sender = sender;
        this.repo = repo;
    }


    @RabbitListener(queues = {
            RabbitmqConfig.QUEUE_UPDATEPOINT_REQUEST
    })
    public void messageReceiver(String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel ch) throws IOException {
//        decode the message
        UpdatePointsEnvelope message = gson.fromJson(msg, UpdatePointsEnvelope.class);





    }




}
