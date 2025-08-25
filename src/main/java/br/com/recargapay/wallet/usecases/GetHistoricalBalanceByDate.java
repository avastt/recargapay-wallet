package br.com.recargapay.wallet.usecases;

import br.com.recargapay.wallet.exception.NotFoundException;
import br.com.recargapay.wallet.models.TransactionModel;
import br.com.recargapay.wallet.repository.TransactionRepository;
import br.com.recargapay.wallet.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static br.com.recargapay.wallet.exception.ErrorKey.TRANSACTION_NOT_FOUND;


@Component
@RequiredArgsConstructor
public class GetHistoricalBalanceByDate {

	public static final int DAYS_TO_ADD = 1;
	private final TransactionRepository transactionRepository;

	private final MessageUtils messageUtils;

	public BigDecimal execute(final UUID id, final LocalDate date) {
		LocalDateTime startOfDay = date.atStartOfDay();
		LocalDateTime startOfNextDay = date.plusDays(DAYS_TO_ADD).atStartOfDay();

		var lastTransactionOfTheDay = transactionRepository.findByCreatedAtBetween(id, startOfDay, startOfNextDay)
				.stream()
				.map(TransactionModel::toDomain)
				.findFirst()
				.orElseThrow(() -> new NotFoundException(messageUtils.getMessage(TRANSACTION_NOT_FOUND, id)));

        return lastTransactionOfTheDay.getHistoricalBalance();
	}

}
