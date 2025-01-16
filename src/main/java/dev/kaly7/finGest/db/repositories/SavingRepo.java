package dev.kaly7.finGest.db.repositories;

import dev.kaly7.finGest.entities.Saving;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingRepo extends JpaRepository<Saving, Integer> {
}
