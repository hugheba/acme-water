package com.acme

import com.acme.model.User
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
/**
 * @author Bryan Hughes <hugheba@gmail.com>
 */
@Slf4j
@CompileStatic
@Component
class BillingSchedule {

    @Autowired BillingService billingService

    /**
     * Simulate some other external process.
     */
    @Scheduled(initialDelay = 10000L, fixedDelay = 60000L)
    void mailBills() {
        /*
        Normally I would queue each individual actor in AMQP, DB, etc. so that it's a little more fault tolerant,
        but for sake of example will directly call
         */
        billingService.acmeRecords.each { User user ->
            billingService.mailBill(user)
        }
    }
}
