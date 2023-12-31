package com.michalenok.wallet.integration;

import com.michalenok.wallet.kafka.listener.TransferMessageListenerImpl;
import com.michalenok.wallet.model.dto.request.TransferRequestDto;
import com.michalenok.wallet.model.dto.response.TransferInfoDto;
import com.michalenok.wallet.model.exception.AccountNotFoundException;
import com.michalenok.wallet.service.api.AccountService;
import com.michalenok.wallet.service.api.TransferService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

 class TransferServiceIntegrationImplTest extends IntegrationTestBase {
    @Autowired
    private TransferService transferService;
    @Autowired
    private AccountService accountService;
    @MockBean
    private TransferMessageListenerImpl transferMessageListener;

    @Test
    void debitTransfer_Successful() {
        UUID accountId = UUID.fromString("ad16c450-386f-427b-a5e5-763577425e5d");
        String reference = "80c025b6-cb71-48aa-adff-053ddbfb3838";
        BigDecimal transferAmount = new BigDecimal(100);
        BigDecimal currentBalanceBeforeTransfer = accountService.findByAccountId(accountId).currentBalance();
        TransferRequestDto transfer = new TransferRequestDto (
                accountId,
                reference,
                new BigDecimal(100),
                "EUR");
        TransferInfoDto transferInfoDto = transferService.debitTransfer(transfer);
        assertEquals("Transfer successfully completed", transferInfoDto.message());
        assertEquals(currentBalanceBeforeTransfer.add(transferAmount), accountService.findByAccountId(accountId).currentBalance());
    }

    @Test
    void debitTransfer_AccountNotFoundException() {
        UUID accountId = UUID.fromString("ad20c450-386f-427b-a5e5-763577425e5d");
        String reference = "80c025b6-cb71-48aa-adff-053ddbfb3838";
        TransferRequestDto transfer = new TransferRequestDto (
                accountId,
                reference,
                new BigDecimal(100),
                "EUR");
        assertThrows(AccountNotFoundException.class, () -> transferService.debitTransfer(transfer));
    }

    @Test
    void creditTransfer_Successful() {
        UUID accountId = UUID.fromString("767762e6-85df-48b0-bc60-ff0520416e16");
        String reference = "80c025b6-cb71-48aa-adff-053ddbfb3838";
        BigDecimal transferAmount = new BigDecimal(100);
        BigDecimal currentBalanceBeforeTransfer = accountService.findByAccountId(accountId).currentBalance();
        TransferRequestDto transfer = new TransferRequestDto (
                accountId,
                reference,
                transferAmount,
                "EUR");
        TransferInfoDto transferInfoDto = transferService.creditTransfer(transfer);
        assertEquals("Transfer successfully completed", transferInfoDto.message());
        assertEquals(currentBalanceBeforeTransfer.add(transferAmount.negate()), accountService.findByAccountId(accountId).currentBalance());
    }

    @Test
    void creditTransfer_AccountNotFoundException() {
        UUID accountId = UUID.fromString("ad20c450-386f-427b-a5e5-763577425e5d");
        String reference = "80c025b6-cb71-48aa-adff-053ddbfb3838";
        TransferRequestDto transfer = new TransferRequestDto (
                accountId,
                reference,
                new BigDecimal(100),
                "EUR");
        assertThrows(AccountNotFoundException.class, () -> transferService.creditTransfer(transfer));
    }

    @Test
    void internalFundTransfer_Successful() {
        UUID accountIdTo = UUID.fromString("767762e6-85df-48b0-bc60-ff0520416e16");
        UUID accountIdFrom = UUID.fromString("ad16c450-386f-427b-a5e5-763577425e5d");
        BigDecimal transferAmount = new BigDecimal(100);
        BigDecimal currentBalanceBeforeTransferTo = accountService.findByAccountId(accountIdTo).currentBalance();
        BigDecimal currentBalanceBeforeTransferFrom = accountService.findByAccountId(accountIdFrom).currentBalance();

        TransferRequestDto transfer = new TransferRequestDto (
                accountIdTo,
                accountIdFrom.toString(),
                new BigDecimal(100),
                "EUR");
        TransferInfoDto transferInfoDto = transferService.internalFundTransfer(transfer);
        assertEquals("Transfer successfully completed", transferInfoDto.message());
        assertEquals(currentBalanceBeforeTransferTo.add(transferAmount), accountService.findByAccountId(accountIdTo).currentBalance());
        assertEquals(currentBalanceBeforeTransferFrom.add(transferAmount.negate()), accountService.findByAccountId(accountIdFrom).currentBalance());
    }

    @Test
    void internalFundTransfer_AccountNotFoundException() {
        UUID accountId = UUID.fromString("ad20c450-386f-427b-a5e5-763577425e5d");
        String reference = "767762e6-85df-48b0-bc60-ff0520416e16";
        TransferRequestDto transfer = new TransferRequestDto (
                accountId,
                reference,
                new BigDecimal(100),
                "EUR");
        assertThrows(AccountNotFoundException.class, () -> transferService.internalFundTransfer(transfer));
    }
}