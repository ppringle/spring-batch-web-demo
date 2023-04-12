package mx.nmp.mipp.customer.job.engine.batch.job.samplejob;

import lombok.extern.slf4j.Slf4j;
import mx.nmp.mipp.customer.job.engine.batch.domain.Customer;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class CustomerProcessor implements ItemProcessor<Customer, Customer> {

    @Override
    public Customer process(final Customer customer) {

        final String firstName = customer.getFirstName().toUpperCase();
        final String lastName = customer.getLastName().toUpperCase();
        final Customer transformedCustomer = new Customer(1L, firstName, lastName);
        log.info("Converting (" + customer + ") into (" + transformedCustomer + ")");
        return transformedCustomer;

    }

}