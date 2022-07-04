package org.technbolts.customers;

import com.github.javafaker.Faker;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.technbolts.customers.jdbc.JdbcCustomers;
import org.technbolts.shared.BasicCriteria;
import org.technbolts.shared.Page;
import org.technbolts.shared.Paging;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class UsecasesIntegrationTests {

  private JdbcCustomers customers;
  private Faker faker;

  @BeforeEach
  void setUp() {
    faker = new Faker(Locale.FRENCH);
    customers = new JdbcCustomers();
  }

  @Test
  void generateRandomData() throws IOException {
    generateCSVFile("target/customers.csv", 500);
  }

  private void generateCSVFile(String fileName, int count) throws IOException {
    CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.ALL).withHeader("email", "firstname", "lastname", "localized_labels");
    CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(fileName), format);

    for (int i = 0; i < count; i++) {
      NewCustomer n = randomNewCustomer();
      csvPrinter.printRecord(n.email(), n.firstname(), n.lastname(), n.localizedLabels().toString());
    }
    csvPrinter.flush();
  }

  @Test
  void hugeGenerateData() throws IOException {
    int count = 100000;
    String fileName = "target/customers" + count + ".csv";
    generateCSVFile(fileName, count);
    List<NewCustomer> newCustomers = loadNewCustomers(new InputStreamReader(new FileInputStream(fileName)));

    partition(newCustomers, 10)
            .forEach(ns -> insertSamplesIfMissing(ns));

  }

  @Test
  void case0001() throws Exception {
    // use known data; @see #generateRandomData for more
    List<NewCustomer> newCustomers = loadNewCustomers(new InputStreamReader(getClass().getResourceAsStream("/samples/customers.csv")));
    insertSamplesIfMissing(newCustomers);

    BasicCriteria<Customer.Column> criteria = new BasicCriteria<>(Customer.Column.EMAIL, "maëlle");
    Page<Customer> page = customers.search(new Paging(5, null), criteria);
    assertThat(page).isNotNull();
    assertThat(page.values()).isNotEmpty();
  }

  private void insertSamplesIfMissing(List<NewCustomer> newCustomers) {
    try {
      List<CustomerId> ids = customers.add(newCustomers);
      assertThat(ids).allMatch(Objects::nonNull);
      assertThat(ids).hasSameSizeAs(newCustomers);
    } catch (RuntimeException e) {
      if (e.getCause() instanceof BatchUpdateException) {
        BatchUpdateException bue = (BatchUpdateException) e.getCause();
        assertThat(bue.getMessage()).contains("already exists.");
      } else {
        throw e;
      }
    }
  }

  private List<NewCustomer> loadNewCustomers(InputStreamReader reader) throws IOException {
    CSVFormat format = CSVFormat.DEFAULT
            .withQuoteMode(QuoteMode.ALL)
            .withFirstRecordAsHeader()
            .withHeader("email", "firstname", "lastname", "localized_labels");
    CSVParser csvParser = new CSVParser(reader, format);
    List<NewCustomer> newCustomers = new ArrayList<>();
    for (CSVRecord record : csvParser) {
      String localizedLabels = record.get("localized_labels");
      newCustomers.add(new NewCustomer(record.get("email"), record.get("firstname"), record.get("lastname"), new JSONObject(localizedLabels)));
    }
    return newCustomers;
  }

  private NewCustomer randomNewCustomer() {
    String firstname = faker.name().firstName();
    String lastname = faker.name().lastName();
    String email = firstname + "." + lastname + "@" + faker.internet().domainName();
    int n = faker.random().nextInt(100);
    if (n < 10) {
      email = "" + firstname.charAt(0) + "." + lastname + "@" + faker.internet().domainName();
    } else if (n < 25) {
      email = firstname + "@" + lastname + ".com";
    } else if (n < 30) {
      email = firstname + "@" + lastname + ".org";
    }

    return new NewCustomer(email, firstname, lastname, new JSONObject());
  }

  public static <T> Collection<List<T>> partition(List<T> xs, int batchSz) {
    AtomicInteger counter = new AtomicInteger();

    return xs
            .stream()
            .collect(Collectors.groupingBy(gr -> counter.getAndIncrement() / batchSz))
            .values();
  }
}
