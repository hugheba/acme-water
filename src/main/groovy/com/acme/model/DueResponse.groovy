package com.acme.model

import com.fasterxml.jackson.annotation.JsonProperty
/**
 * @author Bryan Hughes <hugheba@gmail.com>
 */
class DueResponse implements Serializable {
    @JsonProperty("amount_due")
    BigDecimal amountDue
}
