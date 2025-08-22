package br.com.recargapay.wallet.controllers;


import br.com.recargapay.wallet.controllers.inputs.requests.CreateWalletRequest;
import br.com.recargapay.wallet.controllers.inputs.responses.WalletResponse;
import br.com.recargapay.wallet.usecases.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

import static br.com.recargapay.wallet.controllers.inputs.responses.WalletResponse.createResponse;

@Log4j2
@RestController
@RequestMapping("/wallet")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Wallet")
public class WalletController {

	private CreateUserWallet createWallet;
	private GetUserBalance getUserBalance;

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
			@ApiResponse(responseCode = "200", description = "OK!"),
			@ApiResponse(responseCode = "400", description = "Bad request. Check request and try again."),
			@ApiResponse(responseCode = "404", description = "User wallet not found.")
	})
	public ResponseEntity<BigDecimal> userWalletBalance(@PathVariable final UUID id) {

		log.debug("GET user wallet balance: {} ", id.toString());
		var balance = getUserBalance.execute(id);

		return ResponseEntity.status(HttpStatus.OK).body(balance);
	}

}
