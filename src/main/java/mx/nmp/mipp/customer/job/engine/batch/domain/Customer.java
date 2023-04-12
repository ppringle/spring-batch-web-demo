package mx.nmp.mipp.customer.job.engine.batch.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Customer {

    private Long id;

    private String lastName;

    private String firstName;

}