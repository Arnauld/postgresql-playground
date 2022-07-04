package org.technbolts.customers;

import org.json.JSONObject;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class Customer {

  public enum Column {
    ID,
    EMAIL,
    FIRSTNAME,
    LASTNAME
  }

  private final CustomerId id;
  private final String email;
  private final String firstname;
  private final String lastname;
  private final JSONObject localizedLabels;

  public Customer(CustomerId id, String email, String firstname, String lastname, JSONObject localizedLabels) {
    this.id = id;
    this.email = email;
    this.firstname = firstname;
    this.lastname = lastname;
    this.localizedLabels = localizedLabels;
  }

  @Override
  public String toString() {
    return "Customer{" + id +
            ", '" + email + '\'' +
            ", firstname='" + firstname + '\'' +
            ", lastname='" + lastname + '\'' +
            ", localizedLabels=" + localizedLabels +
            '}';
  }
}
