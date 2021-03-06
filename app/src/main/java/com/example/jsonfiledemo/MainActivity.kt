package com.example.jsonfiledemo

import android.content.res.AssetManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader


class MainActivity : AppCompatActivity() {

    private lateinit var assetManager: AssetManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        assetManager = resources.assets //アセット呼び出し

        val testCity = "那覇市"
        val cityId:Int = searchCityId(testCity)
        println(testCity + "のIDは : " + cityId)

        val testCategory:Array<String> = arrayOf("history", "trivia")
        val categoriesId: Array<Int> = searchCategoryId(*testCategory)
        testCategory.forEach { print(it + " ") }
        print("のIDは : ")
        categoriesId.forEach { print(it.toString() + " ") }
        println()

        val contentsIdAndArticleId:Array<Array<Int>> = searchContentIdAndArticleId(cityId, categoriesId)
        for(c in contentsIdAndArticleId){
            println("ContentID and ArticleID 2次元配列　：" + c.contentToString())
        }

        val contentsId: Array<Int>  = contentsIdAndArticleId[0]
        val articleId: Array<Int>  = contentsIdAndArticleId[1]
        println("Content_ID : " + contentsId.contentToString() + "  Article_ID : " + articleId.contentToString())
        println()

    }

    private fun searchContentIdAndArticleId(cityId: Int, categoriesIds:Array<Int>):Array<Array<Int>>{
        val contentsFile = assetManager.open("Contents.json") //Contents Jsonファイル
        val contentsBR = BufferedReader(InputStreamReader(contentsFile))
        val contentsString: String = contentsBR.readText() //データ
        val contentsArray = JSONArray(contentsString)

        var contentsId: Array<Int>  = arrayOf()
        var articleId: Array<Int>  = arrayOf()
        var contentsIdAndArticleId  = arrayOf<Array<Int>>()

        for(i in 0 until contentsArray.length()) {
            val content = contentsArray.getJSONObject(i)
            val contentCityId = content.getInt("city_id")
            println("Contents.json : " + content)

            if(contentCityId.equals(cityId)) {
                val contentId = content.getInt("content_id")

                categoriesIds.forEach {
                    val category_id = content.getInt("category_id")

                    if (category_id.equals(it)) {
                        println("CityIDとCategoryIDによって抽出されたcontent ID : " + contentId)
                        contentsId+= contentId

                        val article_Id = content.getInt("article_id")
                        articleId+= article_Id
                        println("CityIDとCategoryIDによって抽出されたarticle ID : " + article_Id)
                    }
                }
            }
        }

        contentsIdAndArticleId = arrayOf(contentsId, articleId)
        return contentsIdAndArticleId
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

    private fun searchCityId(selectedCity:String):Int{

        val cities = assetManager.open("Cities.json") //Cities Jsonファイル
        val city = BufferedReader(InputStreamReader(cities))
        val cityStr: String = city.readText() //データ
        val citiesJsonArray = JSONArray(cityStr)

        val selectedCityName:String = selectedCity
        val selectedCityId:Int

        for(i in 0 until citiesJsonArray.length()){
            val cityName = citiesJsonArray.getJSONObject(i).getString("name").toString()

            if(selectedCityName.equals(cityName)){
                selectedCityId = citiesJsonArray.getJSONObject(i).getInt("id")
                return selectedCityId
            }
        }
        return 0
    }
}