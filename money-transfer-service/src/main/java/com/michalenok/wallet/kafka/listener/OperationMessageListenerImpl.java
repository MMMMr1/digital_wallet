package com.michalenok.wallet.kafka.listener;

import com.michalenok.wallet.kafka.listener.api.MessageListener;
import com.michalenok.wallet.kafka.schema.Transaction;
import com.michalenok.wallet.mapper.OperationMapper;
import com.michalenok.wallet.repository.OperationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.UUID;
import static com.michalenok.wallet.kafka.schema.TransactionStatus.SUCCESSFUL;
import static com.michalenok.wallet.model.enums.OperationStatus.FAILED;
import static com.michalenok.wallet.model.enums.OperationStatus.COMPLETED;

@Log4j2
@Component
@RequiredArgsConstructor
@KafkaListener(topics = "${kafka.topic.transaction}", groupId = "${spring.kafka.consumer.group-id}")
public class OperationMessageListenerImpl implements MessageListener<Transaction> {
    private final OperationRepository repository;
    private final OperationMapper operationMapper;
    @KafkaHandler
    public void listenMessage(Transaction transaction) {
        log.info("operationmessagelistener {} ", transaction.toString());
        repository.findById(UUID.fromString(transaction.getUuid()))
                         .ifPresent(entity -> entity.setStatus(
                                 transaction.getTransactionStatus().equals(SUCCESSFUL.name()) ?
                                         COMPLETED : FAILED
                         ));
    }
}
