package dev.kaly7.fingest.db.repositories;

import dev.kaly7.fingest.entities.User;
import dev.kaly7.fingest.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletRepo extends JpaRepository<Wallet, Integer> {

    // Method to retrieve wallets by user and sort by id
    List<Wallet> findByUserOrderByIdAsc(User user);
}
