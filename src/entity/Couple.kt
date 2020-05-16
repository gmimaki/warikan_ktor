package com.example.entity

import com.example.dao.Couples
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class Couple(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Couple>(Couples)

    var name1 by Couples.name1
    var name2 by Couples.name2
}
