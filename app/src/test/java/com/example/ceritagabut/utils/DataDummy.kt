package com.example.ceritagabut.utils

import com.example.ceritagabut.responses.ListResultItems

object DataDummy {
    fun generateDummyItemEntity(size: Int): List<ListResultItems> {
        val itemList = ArrayList<ListResultItems>()
        for (i in 1..size) {
            val itemResults = ListResultItems(
                i.toString(),
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/small/avatar/dos-1760043e025fb28eaf6ff197f02b293820240101203731.png",
                "Wafiq Muhaz",
                "Profile Picture",
                "2024-04-08",
                107.4799072,
                -7.0314509
            )
            itemList.add(itemResults)
        }
        return itemList
    }
}