package com.acme.model

import groovy.transform.EqualsAndHashCode
import org.apache.commons.csv.CSVRecord
/**
 * @author Bryan Hughes <hugheba@gmail.com>
 */
@EqualsAndHashCode
class User implements Serializable {

    String uuid
    String name
    String email
    String address
    String city
    String state
    String zip

    User(CSVRecord record) {
        setUuid(record.get('uuid'))
        setName(record.get('name'))
        setEmail(record.get('email'))
        setAddress(record.get('address'))
        setCity(record.get('city'))
        setState(record.get('state'))
        setZip(record.get('zip'))
    }

    String toString() {
        "${uuid}: ${name}"
    }
}
