package edu.prz.techbank.processor.spark;

import edu.prz.techbank.processor.domain.transaction.Transaction;
import edu.prz.techbank.processor.domain.turnover.Turnover;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/processing")
@Slf4j
public class SparkProcessingController {

  final SparkTransactionService transactionService;
  final SparkTurnoverService turnoverService;
  final TurnoverCalculationJob turnoverCalculationJob;

  @PostMapping("/turnover")
  public ResponseEntity<String> runTurnoversCalculation(@RequestParam LocalDate date) {

    turnoverCalculationJob.run(date);

    return ResponseEntity.ok().build();
  }

  @GetMapping("/transactions")  // dodaję drugi parametr - account - jako dodatkowy
  public ResponseEntity<List<Transaction>> getTransactions(@RequestParam LocalDate date, @RequestParam(required = false) String account) {

    // nowo dodany parametr należy również przekazywać do serwizu
    return ResponseEntity.ok(transactionService.getTransactionsAsList(date, account));
    // zaktualizowałem również plik SparkTransactionService, by serwis obsługiwał zapytania z obydwoma parametrami
  }

  @GetMapping("/turnover")
  public ResponseEntity<List<Turnover>> getTurnover(@RequestParam String account) {

    return ResponseEntity.ok(turnoverService.getOrderedTurnoverAsList(account));
  }

}
