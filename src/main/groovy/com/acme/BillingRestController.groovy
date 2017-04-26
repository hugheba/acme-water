package com.acme

import com.acme.model.DueResponse
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
/**
 * @author Bryan Hughes <hugheba@gmail.com>
 */
@Slf4j
@CompileStatic
@RestController
class BillingRestController {

    @Autowired BillingService billingService

    @RequestMapping( value = "/due/{UUID}/{MONTH}/{YEAR}", method = RequestMethod.GET, produces = "application/json")
    DueResponse due(@PathVariable String UUID, @PathVariable Integer MONTH, @PathVariable Integer YEAR) {
        DueResponse response = new DueResponse()
        try {
            response.amountDue = billingService.getAmountDue(UUID, MONTH, YEAR)
        } catch(Exception e) {
            log.error "Unable to process request /due/${UUID}/${MONTH}/${YEAR}", e
        }
        return response
    }

}
