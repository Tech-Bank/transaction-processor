package edu.prz.techbank.processor.spark;

import edu.prz.techbank.processor.domain.transaction.Transaction;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import apache.spark.sql.functions;

@Service
@RequiredArgsConstructor
public class SparkTransactionService {

  @Value("${hdfs.uri}")
  String hdfsUri;

  @Value("${processing.transaction.path}")
  String path;

  final SparkSession spark;

  public Dataset<Transaction> getTransactions(LocalDate date) {
    return spark
        .read()
        .parquet(hdfsUri + path + "/date=" + date)
        .as(Encoders.bean(Transaction.class));
  }

  // aktualizuję metodę dodając parametr account
  public List<Transaction> getTransactionsAsList(LocalDate date, String account) {

    // podstawowy zbiór przefiltrowany po dacie, ponieważ data jest wymaganym parametrem. Konto - jako opcjonalny - będzie kolejnym filtrem
    Dataset<Transaction> dataset = getTransactions(date);

    // sprawdzenie czy dodatkowy parametr został wprowadzony w zapytaniu
    if (account != null) {
        // filtruje wiersze, gdzie kolumna sender == account lub kolumna beneficiary == account
        dataset = dataset.filter(
            functions.col("sender").equalTo(account)
            .or(functions.col("beneficiary").equalTo(account))
        );
    }
    // wynik jako przefiltrowana lista
    return dataset.collectAsList();
  }

}
