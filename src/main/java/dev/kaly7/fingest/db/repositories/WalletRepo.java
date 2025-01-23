package dev.kaly7.fingest.db.repositories;

import dev.kaly7.fingest.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepo extends JpaRepository<Wallet, Integer> {
}
