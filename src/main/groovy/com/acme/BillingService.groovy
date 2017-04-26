package com.acme

import com.acme.model.BillMessage
import com.acme.model.BillTracking
import com.acme.model.DueResponse
import com.acme.model.User
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

import java.math.RoundingMode
import java.time.LocalDateTime
import java.time.Month
/**
 * @author Bryan Hughes <hugheba@gmail.com>
 */
@Slf4j
@CompileStatic
@Service
class BillingService {

    @Autowired List<User> acmeRecords
    private final RestTemplate restTemplate = new RestTemplate()
    /**
     * Normally would persist this in database,
     */
    private final Map<String, BillTracking> monthlyBillLog = [:]

    /**
     * Calculates amount_due
     *
     * @param uuid
     * @param month
     * @param year
     * @return
     */
    BigDecimal getAmountDue(String uuid, Integer month, Integer year) {
        // Whatever business logic
        new BigDecimal("${year}.${month}").setScale(2, RoundingMode.HALF_EVEN)
    }

    /**
     * Returns user
     *
     * @param uuid
     * @return
     */
    User getUserByUUID(String uuid) {
        acmeRecords.find{it.uuid==uuid}
    }

    /**
     * Wrapper call to API /due/UUID/MONTH/YEAR
     *
     * @param uuid
     * @param month
     * @param year
     * @return
     */
    BigDecimal callAmountDueApi(String uuid, Integer month, Integer year) {
        String endpoint = "http://localhost:8381/due/${uuid}/${month}/${year}"
        DueResponse dueResponse =
                restTemplate.getForObject(endpoint, DueResponse)
        return dueResponse.amountDue
    }

    /**
     * Quick high level process to send bill to User from CSV
     */
    void mailBill(User user) {
        LocalDateTime billDate = LocalDateTime.now()
        // Use this month
        Month billMonth = billDate.month
        // Use this year
        Integer billYear = billDate.year

        // Generate some key for tracking status of bill send
        String billkey = "${user.uuid}-${billYear}-${billMonth.value}"

        // Extra checking from database if bill should be sent
        if(!monthlyBillLog.containsKey(billkey)) {

            // Call API and get amount_due
            BigDecimal amountDue = callAmountDueApi(user.uuid, billMonth.value, billYear)

            // Generate the full message from template
            String messageBody = """Dear ${user.name},
Thank you for using Acme WaterTM for your address at ${user.address} ${user.city}, ${user.state} ${user.zip}.
Your amount due for the month of ${billMonth} is \$${amountDue.setScale(2, RoundingMode.HALF_EVEN)}.
Warm Regards, Acme WaterTM"""

            // Track and queue message
            queueMessage(new BillMessage(dtStamp: billDate, userUUID: user.uuid, message: messageBody))

            // Cleanup
            cleanup(billkey)

        }
    }

    /**
     * Adds message to Que for delivery.
     *
     * Separate process sends email through service like SendGrid,
     * or generates PDF for USPS delivery through service like
     * Click2Mail
     *
     * Pull status from external delivery service and update database.
     *
     * @param billKey
     * @param user
     * @param messageBody
     */
    void queueMessage(BillMessage msg) {
        log.info "Sent message to ${getUserByUUID(msg.userUUID).email}\n${msg.message}"
    }

    /**
     * Cleanup up database status, tracking, etc. Clear record from CSV if needed.
     *
     * @param billKey
     * @param user
     * @param month
     * @param year
     */
    void cleanup(String billKey) {
        monthlyBillLog.put(billKey, new BillTracking())
        log.info "Cleaned up record"
    }
}
