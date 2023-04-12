package mx.nmp.mipp.customer.job.engine.batch.job.samplejob.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.nmp.mipp.customer.job.engine.batch.domain.Customer;
import mx.nmp.mipp.customer.job.engine.batch.job.samplejob.CustomerProcessor;
import mx.nmp.mipp.customer.job.engine.batch.job.samplejob.JobCompletionNotificationListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SampleJobConfig {

    private final JobRepository jobRepository;

    @Bean
    public FlatFileItemReader<Customer> reader() {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("customerReader")
                .resource(new ClassPathResource("data.csv"))
                .delimited()
                .names(new String[]{"firstName", "lastName"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(Customer.class);
                }})
                .build();
    }

    @Bean
    public ItemWriter<Customer> itemWriter() {
        return results -> {
            log.info("New chunk being processed");
            int index = 0;
            for (Customer customer : results) {
                log.info("Value ({}) in chunk: <{}>", index, customer);
                index++;
            }
        };
    }

    @Bean
    public CustomerProcessor processor() {
        return new CustomerProcessor();
    }

    @Bean
    public Step step1(ItemReader<Customer> itemReader, ItemWriter<Customer> itemWriter,
                      CustomerProcessor customerProcessor, PlatformTransactionManager transactionManager) {

        return new StepBuilder("exportCustomerRecordsToStdOut", jobRepository)
                .<Customer, Customer>chunk(10, transactionManager)
                .reader(itemReader)
                .processor(customerProcessor)
                .writer(itemWriter)
                .build();
    }

    @Bean
    public Job customerPrintJob(JobCompletionNotificationListener listener, Step step1) {
        return new JobBuilder("sampleJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(step1)
                .build();
    }

}
