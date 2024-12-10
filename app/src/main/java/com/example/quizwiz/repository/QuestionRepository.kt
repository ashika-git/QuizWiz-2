package com.example.quizwiz.repository

import android.util.Log
import com.example.quizwiz.data.DataOrException
import com.example.quizwiz.model.QuestionItem
import com.example.quizwiz.network.QuestionApi
import javax.inject.Inject

class QuestionRepository @Inject constructor(
    private val api : QuestionApi){
//    private val listOfQuestion = ArrayList<QuestionItem>(emptyList())
    private val dataOrException =
        DataOrException<
                ArrayList<QuestionItem>,
                Boolean,
                Exception
                >()

    suspend fun getAllQuestions(): DataOrException<ArrayList<QuestionItem>, Boolean, Exception>{
        try {
            dataOrException.loading = true
            dataOrException.data = api.getAllQuestions()
            if (dataOrException.data.toString().isNotEmpty()) dataOrException.loading = false

        }catch (exeption: Exception){
            dataOrException.e = exeption
            Log.d("execption", "getAllQuestion: ${dataOrException.e!!.localizedMessage}")
        }

        return dataOrException
    }

}