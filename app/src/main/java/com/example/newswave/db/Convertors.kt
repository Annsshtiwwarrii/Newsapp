package com.example.newswave.db

import androidx.room.TypeConverter
import com.example.newswave.models.Source

class Convertors {
    @TypeConverter
    fun fromSource(source: Source):String{
        return source.name
    }
    @TypeConverter
    fun toSource(name: String):Source{
        return Source(name,name)
    }


//    @TypeConverter
//    fun fromAnyToString(attribute:Any?):String{
//        if (attribute == null)
//            return  ""
//        return attribute as String
//
//
//    }
    @TypeConverter
    fun fromStringToAny(attribute:String?):Any{
        if (attribute == null)
            return  ""
        return attribute
    }

}