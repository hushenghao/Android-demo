package com.example.sqlitetrigger

class ListItem(val id: Int, val userId: String, val itemId: Int, val date: String) {

    override fun toString(): String {
        return "$id\t,\t$userId\t,\t$itemId\t,\t$date"
    }
}