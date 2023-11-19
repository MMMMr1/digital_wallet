package com.michalenok.wallet.kafka.producer;

import com.michalenok.wallet.kafka.producer.api.MessageProducer;
import com.michalenok.wallet.kafka.schema.Transfer;
import com.michalenok.wallet.mapper.TransferMapper;
import com.michalenok.wallet.model.dto.request.TransferRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Log4j2
@Component
@RequiredArgsConstructor
public class OperationMessageProducerImpl implements MessageProducer<Transfer> {
    private final KafkaTemplate<String, Transfer> operationKafkaTemplate;

    @Override
    public void sendMessage(Transfer transfer, String operationTopicName) {
        CompletableFuture<SendResult<String, Transfer>> future =
                operationKafkaTemplate.send(operationTopicName,transfer);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Send message to topic [" + operationTopicName +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }
            else {
                log.error("Unable to send message to topic [" + operationTopicName +
                        "]  due to : " + ex.getMessage());
            }
        });
    }
}

