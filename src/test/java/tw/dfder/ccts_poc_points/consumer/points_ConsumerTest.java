package tw.dfder.ccts_poc_points.consumer;


import au.com.dius.pact.consumer.MessagePactBuilder;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.consumer.junit5.ProviderType;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.messaging.Message;
import au.com.dius.pact.core.model.messaging.MessagePact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import tw.dfder.ccts_poc_points.configuration.ServiceConfig;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "orchestrator", providerType = ProviderType.ASYNCH)
public class points_ConsumerTest {

    @Pact(consumer = "pointService")
    public MessagePact validatePointMessageSenderContractBuilder(MessagePactBuilder builder){
        return builder
                .expectsToReceive("t-orc-point-01")
                .withMetadata( m -> {
                    m.add("provider", "orchestrator");
                    m.add("consumer", "pointService");
                })
                .toPact();
    }


    @Pact(consumer = "pointService")
    public MessagePact validatePointMessageSenderContractBuilder02(MessagePactBuilder builder){
        return builder
                .expectsToReceive("t-orc-point-02")
                .withMetadata( m -> {
                    m.add("provider", "orchestrator");
                    m.add("consumer", "pointService");
                })
                .toPact();
    }


    @Pact(consumer = "pointService")
    public MessagePact validatePointMessageSenderContractBuilder03(MessagePactBuilder builder){
        return builder
                .expectsToReceive("t-orc-point-03")
                .withMetadata( m -> {
                    m.add("provider", "orchestrator");
                    m.add("consumer", "pointService");
                })
                .toPact();
    }


    @Test
    @PactTestFor(pactMethod = "validatePointMessageSenderContractBuilder")
    public void validatePointMessageSenderContractBuilderTest(List<Message> messages){

        // 起碼有上面的案例吧
        assertThat(messages).isNotEmpty();
        // 驗header
        messages.forEach(m -> {
            assertThat(m.getMetadata()).hasFieldOrProperty("provider");
            assertThat(m.getMetadata()).hasFieldOrProperty("consumer");
            });
    }

    @Test
    @PactTestFor(pactMethod = "validatePointMessageSenderContractBuilder02")
    public void validatePointMessageSenderContractBuilderTest02(List<Message> messages){

        // 起碼有上面的案例吧
        assertThat(messages).isNotEmpty();
        // 驗header
        messages.forEach(m -> {
            assertThat(m.getMetadata()).hasFieldOrProperty("provider");
            assertThat(m.getMetadata()).hasFieldOrProperty("consumer");
        });
    }

    @Test
    @PactTestFor(pactMethod = "validatePointMessageSenderContractBuilder03")
    public void validatePointMessageSenderContractBuilderTest03(List<Message> messages){

        // 起碼有上面的案例吧
        assertThat(messages).isNotEmpty();
        // 驗header
        messages.forEach(m -> {
            assertThat(m.getMetadata()).hasFieldOrProperty("provider");
            assertThat(m.getMetadata()).hasFieldOrProperty("consumer");
        });
    }


}
