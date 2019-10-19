package com.example.sqlitetrigger

/**
 * db bean
 */
data class ListItem(val id: Int, val userId: String, val itemId: Int, val date: String) {

    override fun toString(): String {
        return "id: $id,    userId: $userId,    itemId: $itemId\n$date"
    }
}