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
                .expectsToReceive("update point message")
                .withMetadata( m -> {
                    m.add("source", "orchestrator");
                    m.add("destination", "pointService");
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
            assertThat(m.getMetadata()).hasFieldOrProperty("source");
            assertThat(m.getMetadata()).hasFieldOrProperty("destination");
            });

    }


}
