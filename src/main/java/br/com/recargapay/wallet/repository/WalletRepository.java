package br.com.recargapay.wallet.repository;

import br.com.recargapay.wallet.models.WalletModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<WalletModel, UUID> {

}
