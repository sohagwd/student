package com.fery.studentapp

import java.security.Timestamp

class Data {
    data class Data(
        var id:String?=null,
        val name:String?=null,
        val email:String?=null,
        val sub:String?=null,
        val birth:String?=null,
        val timestamp: Timestamp?= null
    )
}