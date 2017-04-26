package com.acme.model

import java.time.LocalDateTime
/**
 * @author Bryan Hughes <hugheba@gmail.com>
 */
class BillMessage implements Serializable {
    LocalDateTime dtStamp
    String userUUID
    String message
}
