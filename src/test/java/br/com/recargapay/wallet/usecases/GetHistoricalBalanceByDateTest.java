package br.com.recargapay.wallet.usecases;

import br.com.recargapay.wallet.domains.Transaction;
import br.com.recargapay.wallet.domains.Wallet;
import br.com.recargapay.wallet.exception.NotFoundException;
import br.com.recargapay.wallet.models.TransactionModel;
import br.com.recargapay.wallet.repository.TransactionRepository;
import br.com.recargapay.wallet.usecases.GetHistoricalBalanceByDate;
import br.com.recargapay.wallet.utils.MessageUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GetHistoricalBalanceByDateTest {

	@Mock
	private TransactionRepository transactionRepository;

	@Mock
	private MessageUtils messageUtils;

	@InjectMocks
	private GetHistoricalBalanceByDate getHistoricalBalanceByDate;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void shouldReturnHistoricalBalanceSuccessfully() {
		// GIVEN
		UUID walletId = UUID.randomUUID();
		LocalDate date = LocalDate.of(2023, 10, 1);
		LocalDateTime startOfDay = date.atStartOfDay();
		LocalDateTime startOfNextDay = date.plusDays(1).atStartOfDay();

		Transaction transaction = Transaction.builder()
				.wallet(Wallet.builder()
						.walletName("Test Wallet")
						.build()
				)
				.id(walletId)
				.historicalBalance(BigDecimal.TEN)
				.build();

		when(transactionRepository.findByCreatedAtBetween(walletId, startOfDay, startOfNextDay))
				.thenReturn(List.of(new TransactionModel(transaction)));

		// WHEN
		BigDecimal historicalBalance = getHistoricalBalanceByDate.execute(walletId, date);

		// THEN
		assertEquals(BigDecimal.valueOf(10), historicalBalance);
		verify(transactionRepository, times(1)).findByCreatedAtBetween(walletId, startOfDay, startOfNextDay);
	}

	@Test
	void shouldThrowNotFoundExceptionWhenNoTransactionExists() {
		// GIVEN
		UUID walletId = UUID.randomUUID();
		LocalDate date = LocalDate.of(2023, 10, 1);
		LocalDateTime startOfDay = date.atStartOfDay();
		LocalDateTime startOfNextDay = date.plusDays(1).atStartOfDay();

		when(transactionRepository.findByCreatedAtBetween(walletId, startOfDay, startOfNextDay))
				.thenReturn(List.of());
		when(messageUtils.getMessage(any(), any())).thenReturn("Transaction not found");

		// WHEN & THEN
		assertThrows(NotFoundException.class, () -> getHistoricalBalanceByDate.execute(walletId, date));
		verify(transactionRepository, times(1)).findByCreatedAtBetween(walletId, startOfDay, startOfNextDay);
	}
}
