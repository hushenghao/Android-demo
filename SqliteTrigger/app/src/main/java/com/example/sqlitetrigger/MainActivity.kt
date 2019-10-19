package com.example.sqlitetrigger

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val dbHelper by lazy { DBHelper(this) }

    private val adapter by lazy {
        ArrayAdapter<ListItem>(
            this,
            android.R.layout.simple_list_item_1
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lv_list.adapter = adapter

        bt_add.setOnClickListener {
            val userId = et_user_id.text.toString()
            if (userId.isEmpty()) {
                toast("输入User Id")
                return@setOnClickListener
            }

            val itemId = getItemId()
            insert(userId, itemId)

            refresh()
        }

        bt_add_one.setOnClickListener {
            insert("10086", 111111)

            refresh()
        }

        lv_list.post {
            refresh()
        }
    }

    private fun insert(userId: String, itemId: Int) {
        val id = dbHelper.query(userId, itemId)
        if (id >= 0) {
            toast("已存在数据 id: $id")
        }
        dbHelper.insert(userId, itemId)
    }

    private fun refresh() {
        val newList = dbHelper.query()
        adapter.clear()
        adapter.addAll(newList)
    }

    private fun getItemId(): Int {
        val random = Random()
        return random.nextInt()
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
