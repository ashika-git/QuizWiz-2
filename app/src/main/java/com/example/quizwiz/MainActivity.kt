 package com.example.quizwiz

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.quizwiz.model.Question
import com.example.quizwiz.screens.QuestionsViewModel
import com.example.quizwiz.ui.theme.QuizWizTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuizWizTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) {
//                    val viewModel by
//                    TriviaHome(viewModel = QuestionsViewModel)
//                }
            }
        }
    }
}

@Composable
fun TriviaHome(viewModel: QuestionsViewModel = hiltViewModel()){
    Questions(viewModel)
}

 @Composable
 fun Questions(viewModel: QuestionsViewModel) {
     val questions = viewModel.data.value.data?.toMutableList()

     if (viewModel.data.value.loading == true){
         Log.d("Loading", "Question..... Loading")
     }else {
         Log.d("Result", "Question: Loading STOPPED..")
         questions?.forEach{questionsItem ->
             Log.d("Result", "Questions: ${questionsItem.question}")

         }
//         if (question != null) {
//             Log.d("SIZE", "Question ${question.size}")
//         }
     }
 }
//
// @Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    QuizWizTheme {
//    }
//}
