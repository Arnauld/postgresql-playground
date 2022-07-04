package org.technbolts.customers;

import org.json.JSONObject;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class NewCustomer {
  private final String email;
  private final String firstname;
  private final String lastname;
  private final JSONObject localizedLabels;

  public NewCustomer(String email, String firstname, String lastname, JSONObject localizedLabels) {
    this.email = email.toLowerCase();
    this.firstname = firstname;
    this.lastname = lastname;
    this.localizedLabels = localizedLabels;
  }

  public String email() {
    return email;
  }

  public String firstname() {
    return firstname;
  }

  public String lastname() {
    return lastname;
  }

  public JSONObject localizedLabels() {
    return localizedLabels;
  }
}
