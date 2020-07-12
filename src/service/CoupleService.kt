package com.example.service

import com.example.entity.Couple
import org.jetbrains.exposed.sql.transactions.transaction

class CoupleService {
    fun getAllCouples(): List<Couple> {
        var result: List<Couple> = listOf()
        transaction {
            result = Couple.all().toList()
        }
        return result
    }

    fun createCouple(name1: String, name2: String, ratio1: Int, ratio2: Int) {
        transaction {
            Couple.new {
                this.name1 = name1
                this.name2 = name2
                this.ratio1 = ratio1
                this.ratio2 = ratio2
            }
        }
    }
}