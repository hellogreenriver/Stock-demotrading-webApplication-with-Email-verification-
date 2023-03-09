package com.example.stockprophet.repository;







import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.stockprophet.model.Symbol;



public interface SymbolRepository extends JpaRepository<Symbol, Long>{
    Optional<List<Symbol>> findBySymbol(String symbol);
}
