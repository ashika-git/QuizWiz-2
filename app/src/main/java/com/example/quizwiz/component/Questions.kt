package com.example.quizwiz.component

import android.provider.CalendarContract
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizwiz.model.QuestionItem
import com.example.quizwiz.screens.QuestionsViewModel
import com.example.quizwiz.util.AppColors

@Composable
fun Questions(viewModel: QuestionsViewModel) {
    val questions = viewModel.data.value.data?.toMutableList()
    val questionIndex = remember {
        mutableIntStateOf(0)
    }

    if (viewModel.data.value.loading == true){

        CircularProgressIndicator()
        Log.d("Loading", "Question..... Loading")
    }else {
//        Log.d("Result", "Question: Loading STOPPED..")
//        questions?.forEach{questionsItem ->
//        Log.d("Result", "Questions: ${questionsItem.question}")

        val question = try {
            questions?.get(questionIndex.value)
        }catch (ex: Exception){
            null
        }

        if (questions != null ){
            QuestionDisplay(question = question!!, questionIndex = questionIndex,
                viewModel= viewModel){
                questionIndex.intValue += 1
            }

        }
    }
}

@Composable
fun QuestionDisplay(
    question: QuestionItem,
    questionIndex : MutableState<Int>,
    viewModel: QuestionsViewModel,
    onNextClicked:(Int) ->Unit

){
    val context = LocalContext.current
    val choicesState = remember(question){
        question.choices.toMutableList()

    }
    val answerState = remember(question){
        mutableStateOf<Int?>(null)
    }
    val correctAnswerState = remember (question){
        mutableStateOf<Boolean?>(null)
    }
    val updateAnswer: (Int) -> Unit = remember(question) {
        {
            answerState.value = it
            correctAnswerState.value = choicesState[it] == question.answer
        }
    }
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    Surface(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .statusBarsPadding() ,

        color = AppColors.mDarkPurple
    ) {

        Column(modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
            ) {

            if (questionIndex.value>= 3) ShowProgress(score = questionIndex.value)
            viewModel.getTotalQuestionCount()?.let { QuestionTracker(questionIndex.value, it) }
            DrawDottedLine(pathEffect)
            Column {
                Text(text = question.question,
                    modifier = Modifier.padding(6.dp)
                        .align(alignment = Alignment.Start)
                        .fillMaxHeight(0.3f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp,
                    color = AppColors.mOffWhite
                )

                //Choices

                choicesState.forEachIndexed{
                    index, answerText ->
                    Row(modifier = Modifier
                        .padding(3.dp)
                        .fillMaxWidth()
                        .height(45.dp)
                        .border(width = 4.dp, brush = Brush.linearGradient(
                            colors = listOf(AppColors.mOffDarkPurple,
                                AppColors.mOffDarkPurple)
                        ),
                            shape = RoundedCornerShape(15.dp))
                        .clip(RoundedCornerShape(topStartPercent = 50,
                            bottomEndPercent = 50,
                            topEndPercent = 50,
                            bottomStartPercent = 50))
                        .background(color = Color.Transparent),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = (answerState.value== index), onClick = {
                            updateAnswer(index)
                        },
                            modifier = Modifier.padding(16.dp),
                            colors = RadioButtonDefaults.colors(
                                selectedColor = if (correctAnswerState.value == true && index == answerState.value){
                                    Color.Green.copy(0.5f)
                                }else{
                                    Color.Red.copy(alpha = 0.5f)
                                }
                            )
                        )
                        //Text
                        val annotatedString = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Light,
                                color =if (correctAnswerState.value == true && index == answerState.value){
                                    Color.Green
                                }else if(correctAnswerState.value == false &&
                                    index == answerState.value){
                                    Color.Red
                                }else{
                                    AppColors.mOffWhite
                                }
                            )
                            ){
                               append(answerText)
                            }
                        }
                        Text(text = annotatedString,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
                Button(onClick ={
                    if (correctAnswerState.value == false){
                        Toast.makeText(context, "Oops select the correct option!!", Toast.LENGTH_SHORT).show()
                    }else{
                        onNextClicked(questionIndex.value)
                    }

                },
                    modifier =Modifier
                    .padding(3.dp)
                    .align(Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(34.dp),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = AppColors.mLightBlue)) {
                    Text(text = "Next",
                        modifier = Modifier.padding(4.dp),
                        color = AppColors.mOffWhite,
                        fontSize = 18.sp
                        )
                }
            }
        }

    }
}


@Composable
fun QuestionTracker(counter : Int, outOf: Int){
    Text(text = buildAnnotatedString {
        withStyle(style = ParagraphStyle(textIndent = TextIndent.None)){
            withStyle(style = SpanStyle(color = AppColors.mLightGrey,
                fontWeight = FontWeight.Bold,
                fontSize = 27.sp
            )){
                append("Question $counter/")
                withStyle(style = SpanStyle(color = AppColors.mLightGrey,
                    fontWeight = FontWeight.Light,
                    fontSize = 20.sp)){
                    append("$outOf")
                }
            }
        }

    },
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
fun DrawDottedLine(pathEffect: PathEffect){
    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxWidth()
        .height(1.dp)) {
        drawLine(color = AppColors.mLightGrey,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = pathEffect
            )
    }
}

//@Preview
@Composable
fun ShowProgress(score:Int = 12){
    val gradient = Brush.linearGradient(listOf(Color(0xFFF95075),
        Color(0xFFBe6BE5)
        ))

    val progressFactor by remember(score){
        mutableStateOf(score*0.005f)
    }

    Row (modifier = Modifier.padding(3.dp)
        .fillMaxWidth()
        .height(45.dp)
        .border(width = 4.dp, brush = Brush.linearGradient(colors = listOf(
            AppColors.mLightPurple, AppColors.mLightPurple
        )),
            shape = RoundedCornerShape(34.dp)
        )
        .clip(RoundedCornerShape(
            topStartPercent = 50,
            topEndPercent = 50,
            bottomStartPercent = 50,
            bottomEndPercent = 50
        ))
        .background(color = Color.Transparent),
        verticalAlignment = Alignment.CenterVertically
    ){
        Button(
            contentPadding = PaddingValues(1.dp),
            onClick = {},
            modifier = Modifier.run {
                fillMaxWidth(progressFactor)
                        .background(brush = gradient)
            },
            enabled = false,
            elevation = null,
            colors = buttonColors(
                containerColor = Color.Transparent,
            )
        ) {

            Text(text = (score* 10).toString(),
                modifier = Modifier.clip(shape = RoundedCornerShape(23.dp))
                    .fillMaxHeight(.87f)
                    .fillMaxWidth()
                    .padding(8.dp),
                color = AppColors.mOffWhite,
                textAlign = TextAlign.Center)
        }
    }
}