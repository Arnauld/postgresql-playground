package org.technbolts.customers.jdbc;

import org.json.JSONObject;
import org.technbolts.customers.Customer;
import org.technbolts.customers.CustomerId;
import org.technbolts.customers.Customers;
import org.technbolts.customers.NewCustomer;
import org.technbolts.shared.BasicCriteria;
import org.technbolts.shared.Page;
import org.technbolts.shared.Paging;
import org.technbolts.tokend.TemplateId;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class JdbcCustomers implements Customers {

  private Connection openConnection() {
    try {
      Class.forName("org.postgresql.Driver");
      return DriverManager.getConnection(
              "jdbc:postgresql://localhost:7001/futurama",
              "futurama_secrets_app",
              "secrets_p");
    } catch (Exception e) {
      throw new RuntimeException("Failed to obtain connection", e);
    }
  }

  @Override
  public List<CustomerId> add(List<NewCustomer> newCustomers) {
    String sql = "insert into customers (" +//
            "email, " + //1
            "firstname, " + //2
            "lastname, " + //3
            "localized_labels" + //4
            ") values (?, ?, ?, ?::JSONB) returning id";
    try (Connection conn = openConnection()) {
      boolean prevAutoCommit = conn.getAutoCommit();
      conn.setAutoCommit(false);

      try (PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"id"})) {
        for (NewCustomer newCustomer : newCustomers) {
          pstmt.setString(1, newCustomer.email());
          pstmt.setString(2, newCustomer.firstname());
          pstmt.setString(3, newCustomer.lastname());
          pstmt.setString(4, newCustomer.localizedLabels().toString());
          pstmt.addBatch();
        }
        // executeBatch does not seem to work with "returning id"...
        // so one relies on generatedKeys...
        pstmt.executeBatch();
        conn.commit();
        ResultSet resultSet = pstmt.getGeneratedKeys();
        List<CustomerId> ids = new ArrayList<>();
        while (resultSet.next()) {
          ids.add(new CustomerId(resultSet.getLong(1)));
        }
        return ids;
      } catch (Exception e) {
        conn.rollback();
        throw e;
      } finally {
        conn.setAutoCommit(prevAutoCommit);
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to insert customers...", e);
    }
  }

  @Override
  public Page<Customer> search(Paging paging, BasicCriteria<Customer.Column> criteria) {
    StringBuilder sql = new StringBuilder("select id, email, firstname, lastname, localized_labels from customers where ");
    String filter = criteria.filter();
    sql.append("to_tsvector(firstname || lastname || email) @@ to_tsquery(?)");

    try (Connection conn = openConnection();
         PreparedStatement ptmt = conn.prepareStatement(sql.toString())) {

      ptmt.setString(1, filter);
      ResultSet resultSet = ptmt.executeQuery();
      List<Customer> customers = new ArrayList<>();
      while (resultSet.next()) {
        customers.add(toCustomer(resultSet));
      }
      return new Page<>(customers, null);

    } catch (Exception e) {
      throw new RuntimeException("Failed to search customers...", e);
    }
  }

  private Customer toCustomer(ResultSet resultSet) throws SQLException {
    return new Customer(
            new CustomerId(resultSet.getLong(1)),
            resultSet.getString(2),
            resultSet.getString(3),
            resultSet.getString(4),
            new JSONObject(resultSet.getString(5))
    );
  }
}
