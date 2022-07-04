package org.technbolts.customers;

import org.technbolts.shared.BasicCriteria;
import org.technbolts.shared.Page;
import org.technbolts.shared.Paging;

import java.util.List;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public interface Customers {
  List<CustomerId> add(List<NewCustomer> newCustomers);
  Page<Customer> search(Paging paging, BasicCriteria<Customer.Column> criteria);
}
