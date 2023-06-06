package pl.lotto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import pl.lotto.domain.numbersgenerator.NumbersGeneratorFacadeConfigurationProperties;
import pl.lotto.infrastucture.numbersgenerator.http.NumbersGeneratorRestTemplateConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({NumbersGeneratorFacadeConfigurationProperties.class, NumbersGeneratorRestTemplateConfigurationProperties.class})
public class LottoSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(LottoSpringBootApplication.class, args);
    }

}