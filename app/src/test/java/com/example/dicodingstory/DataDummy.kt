package com.example.dicodingstory

import com.example.dicodingstory.data.story.response.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "name + $i",
                "description $i",
                "photoUrl $i",
            )
            items.add(story)
        }
        return items
    }
}