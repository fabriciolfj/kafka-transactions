package pl.piomin.services.accounts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.transaction.annotation.Transactional;
import pl.piomin.services.accounts.domain.Account;
import pl.piomin.services.accounts.listener.TransactionsListener;
import pl.piomin.services.accounts.repository.AccountRepository;
import pl.piomin.services.common.model.Order;

import javax.annotation.PostConstruct;
import java.util.Random;

@SpringBootApplication
@EnableKafka
public class AccountsService {

    public static void main(String[] args) {
        SpringApplication.run(AccountsService.class, args);
    }

    private static final Logger LOG = LoggerFactory.getLogger(TransactionsListener.class);

    Random r = new Random();

    @Autowired
    AccountRepository repository;

    @PostConstruct
    public void init() {
        for (int i = 0; i < 100; i++) {
            repository.save(new Account(r.nextInt(1000, 10000)));
        }
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<Integer, String>
    kafkaListenerContainerFactory(ConsumerFactory<Integer, String> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(3);
        factory.getContainerProperties()
                .setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }
}
