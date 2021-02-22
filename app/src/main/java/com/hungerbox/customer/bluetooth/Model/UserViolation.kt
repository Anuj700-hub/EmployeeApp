package com.hungerbox.customer.bluetooth.Model

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable


@DatabaseTable(tableName = "user_violation")
class UserViolation {
    @DatabaseField
    public var contact_id: String? = null

    @DatabaseField
    public var timestamp: Long = 0

    @DatabaseField
    public var location_id: Long = 0

    @DatabaseField
    public var count: Long = 0
}