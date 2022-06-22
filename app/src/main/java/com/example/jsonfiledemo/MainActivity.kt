package com.example.jsonfiledemo

import android.content.res.AssetManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var assetManager: AssetManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        assetManager = resources.assets //アセット呼び出し
        val testCity:String = "那覇市"
        val cityId:Int = searchCityId(testCity)
        println(testCity + "のIDは : " + cityId)

        val testCategory:Array<String> = arrayOf("history", "trivia")
        val categoriesIds: Array<Int> = searchCategoryId(*testCategory)
        testCategory.forEach { print(it + " ") }
        print("のIDは : ")
        categoriesIds.forEach { print(it.toString() + " ") }
        println()

        val contentId:Array<Int> = searchContentId(cityId, categoriesIds)
        print("抽出されたのContentsIDは : ")
        contentId.forEach { print(it.toString() + " ") }
        println()

    }

    private fun searchContentId(cityId: Int, categoriesIds:Array<Int>):Array<Int>{
        val contentsFile = assetManager.open("Contents.json") //Contents Jsonファイル
        val contentsBR = BufferedReader(InputStreamReader(contentsFile))
        val contentsString: String = contentsBR.readText() //データ
        val contentsArray = JSONArray(contentsString)

        var contentsId: Array<Int>  = arrayOf()

        for(i in 0 until contentsArray.length()) {
            val content = contentsArray.getJSONObject(i)
            val contentCityId = content.getInt("city_id")
            println("Contents.json : " + content)

            if(contentCityId.equals(cityId)) {
                val contentId = content.getInt("content_id")

                categoriesIds.forEach {
                    var categoryId = content.getInt("category_id")
                    if (categoryId.equals(it)) {
                        println("CityIDとCategoryIDによって抽出されたcontent ID : " + contentId)
                        contentsId+= contentId
                    }
                }
            }
        }
        return contentsId
    }

    private fun searchCategoryId(vararg selectedCategories: String):Array<Int>{
        val selectedCategoryName: Array<String> = arrayOf(*selectedCategories)

        var categoriesId: Array<Int>  = arrayOf<Int>()

        val categoriesArray = setOf<String>("history", "trivia", "tourist_sights", "restaurants")

        selectedCategoryName.forEach {
            val id = categoriesArray.indexOf(it)+1
            categoriesId+=id
        }
        return categoriesId

    }

    private fun searchCityId(selectedCitiy:String):Int{

        val cities = assetManager.open("Cities.json") //Cities Jsonファイル
        val city = BufferedReader(InputStreamReader(cities))
        val cityStr: String = city.readText() //データ
        val citiesJsonArray = JSONArray(cityStr)

        val selectedCitiyName:String = selectedCitiy
        val selectedCityId:Int

        for(i in 0 until citiesJsonArray.length()){
            val cityName = citiesJsonArray.getJSONObject(i).getString("name").toString()

            if(selectedCitiyName.equals(cityName)){
                selectedCityId = citiesJsonArray.getJSONObject(i).getInt("id")
                return selectedCityId
            }
        }
        return 0
    }
}