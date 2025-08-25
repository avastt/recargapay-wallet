package br.com.recargapay.wallet.controllers;


import br.com.recargapay.wallet.controllers.inputs.requests.CreateWalletRequest;
import br.com.recargapay.wallet.controllers.inputs.requests.TransactionRequest;
import br.com.recargapay.wallet.controllers.inputs.requests.TransferRequest;
import br.com.recargapay.wallet.controllers.inputs.responses.WalletResponse;
import br.com.recargapay.wallet.usecases.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static br.com.recargapay.wallet.controllers.inputs.responses.WalletResponse.createResponse;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/wallet")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Wallet")
public class WalletController {

	private final CreateUserWallet createWallet;
	private final GetUserBalance getUserBalance;
	private final GetHistoricalBalanceByDate getHistoricalBalanceByDate;
	private final DepositAmount depositAmount;
	private final WithdrawAmount withdrawAmount;
	private final TransferFunds transferFunds;

	@PostMapping("/create")
	@Operation(summary = "Create New Wallet",
			responses = {
					@ApiResponse(responseCode = "201", description = "Wallet created successfully!"),
					@ApiResponse(responseCode = "400", description = "Bad request. Check request and try again.")
			})
	public ResponseEntity<WalletResponse> create(@RequestBody @Valid final CreateWalletRequest createWalletRequest) {
		log.debug("POST createWallet received: {} ", createWalletRequest.getWalletName());
		var wallet = createWallet.execute(createWalletRequest.toDomain());

		return ResponseEntity.status(HttpStatus.CREATED).body(createResponse(wallet));
	}

	@GetMapping("/{id}/balance")
	@Operation(summary = "Get User Wallet Balance",
			responses = {
					@ApiResponse(responseCode = "200", description = "User Balance: "),
					@ApiResponse(responseCode = "400", description = "Bad request. Check request and try again."),
					@ApiResponse(responseCode = "404", description = "User wallet not found.")
			})
	public ResponseEntity<BigDecimal> userWalletBalance(@PathVariable final UUID id) {

		log.debug("GET user wallet balance: {} ", id.toString());
		var balance = getUserBalance.execute(id);

		return ResponseEntity.status(HttpStatus.OK).body(balance);
	}

	@PostMapping("/{id}/deposit")
	@Operation(summary = "Adding funds to a wallet",
			responses = {
					@ApiResponse(responseCode = "201", description = "Funds added successfully!"),
					@ApiResponse(responseCode = "400", description = "Bad request. Check request and try again."),
					@ApiResponse(responseCode = "404", description = "Wallet not found. Check request and try again.")
			})
	public ResponseEntity<WalletResponse> deposit(final @PathVariable UUID id,
												  final @RequestBody TransactionRequest amount) {

		var savedWallet = depositAmount.execute(id, amount.getAmount());
		return ResponseEntity.status(HttpStatus.CREATED).body(createResponse(savedWallet));


	}

	@PostMapping("/{id}/withdraw")
	@Operation(summary = "Withdrawn funds from a wallet",
			responses = {
					@ApiResponse(responseCode = "201", description = "Funds withdrawn successfully!"),
					@ApiResponse(responseCode = "400", description = "Bad request. Check request and try again."),
					@ApiResponse(responseCode = "404", description = "Wallet not found. Check request and try again.")
			})
	public ResponseEntity<WalletResponse> withdraw(final @PathVariable UUID id,
												   final @RequestBody TransactionRequest amount) {

		var savedWallet = withdrawAmount.execute(id, amount.getAmount());
		return ResponseEntity.status(HttpStatus.CREATED).body(createResponse(savedWallet));
	}

	@GetMapping("/{id}/historical/balance")
	@Operation(summary = "Get User Wallet Balance by custom period",
			responses = {
					@ApiResponse(responseCode = "200", description = "User balance by period:"),
					@ApiResponse(responseCode = "400", description = "Bad request. Check request and try again."),
					@ApiResponse(responseCode = "404", description = "User wallet not found.")
			})
	public ResponseEntity<BigDecimal> userWalletBalanceByPeriod(@PathVariable final UUID id,
																@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
																final LocalDate date) {
		var balance = getHistoricalBalanceByDate.execute(id, date);
		log.debug("GET historical balance: {} ", balance);
		return ResponseEntity.status(HttpStatus.OK).body(balance);
	}

	@PostMapping("/transfer")
	@Operation(summary = "Transfer funds between wallets",
			responses = {
					@ApiResponse(responseCode = "201", description = "Transfer completed successfully!"),
					@ApiResponse(responseCode = "400", description = "Bad request. Check request and try again."),
					@ApiResponse(responseCode = "404", description = "Wallet not found. Check request and try again.")
			})
	public ResponseEntity<WalletResponse> transfer(final @RequestBody TransferRequest transferRequest) {
		var wallet = transferFunds.execute(transferRequest.getFromWalletId(),
				transferRequest.getToWalletId(), transferRequest.getAmount());
		return ResponseEntity.status(HttpStatus.CREATED).body(createResponse(wallet));
	}
}
