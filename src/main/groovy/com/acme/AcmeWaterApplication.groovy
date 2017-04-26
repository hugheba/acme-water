package com.acme

import com.acme.model.User
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.scheduling.annotation.EnableScheduling

@Slf4j
@CompileStatic
@SpringBootApplication
@ComponentScan
@EnableScheduling
class AcmeWaterApplication {

    static void main(String[] args) {
        SpringApplication.run(AcmeWaterApplication, args)
    }

    /**
     * Loads data from CSV file and builds a list of User entities.
     *
     * Would normally persist this in a less volatile place, but for sake of demo in memory
     *
     * @return List of users
     */
    @Bean(name = 'acmeRecords')
    List<User> getAcmeRecords() {
        List<User> acmeRecords = []
        try {
            CSVFormat.RFC4180.withFirstRecordAsHeader().parse(
                    new InputStreamReader(
                        this.class.classLoader.getResourceAsStream('import.csv')
                    )
            ).each { CSVRecord record ->
                User user = new User(record)

                acmeRecords << user
            }
        } catch(Exception e) {
            log.error "Unable to load CSV", e
        }
        log.info "Loaded ${acmeRecords.size()} records from CSV"
        return acmeRecords
    }

}
