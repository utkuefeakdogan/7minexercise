package com.example.a7minexercise

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a7minexercise.databinding.ActivityExerciseBinding
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var binding:ActivityExerciseBinding? = null

    private var restTimer: CountDownTimer? = null
    private var restProgress: Int = 0

    private var exerciseTimer: CountDownTimer? = null
    private var exerciseProgress: Int = 0

    private var exerciseList: ArrayList<ExerciseModel>?= null
    private var currentExercisePosition: Int = -1

    private var tts: TextToSpeech? = null
    private var player: MediaPlayer? = null

    private var exerciseAdapter: ExerciseStatusAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarExercise)

        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        exerciseList = Constants.defaultExerciseList()

        tts = TextToSpeech(this,this)

        binding?.toolbarExercise?.setNavigationOnClickListener{
            onBackPressed()
        }

        setupRestView()
        setupExerciseStatusRecyclerView()
    }

    private fun setupExerciseStatusRecyclerView(){
        binding?.rvExerciseStatus?.layoutManager =
            LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)

        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!)
        binding?.rvExerciseStatus?.adapter = exerciseAdapter
    }

    private fun setupRestView(){
        try {
            val soundURI = Uri.parse("android.resource://com.example.a7minexercise/" + R.raw.efe_ses)
            player = MediaPlayer.create(applicationContext,soundURI)
            player?.isLooping = false
            player?.start()
        }catch (e: Exception){
            e.printStackTrace()
        }

        binding?.flProgressBar?.visibility= View.VISIBLE
        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.flExerciseView?.visibility= View.INVISIBLE
        binding?.tvExercise?.visibility= View.INVISIBLE
        binding?.ivImage?.visibility= View.INVISIBLE
        binding?.tvupcoming?.visibility= View.VISIBLE
        binding?.tvnextexercisename?.visibility= View.VISIBLE

        if(restTimer!=null){
            restTimer?.cancel()
            restProgress = 0
        }
        binding?.tvnextexercisename?.text = exerciseList!![currentExercisePosition+1].getName()
        setRestProgressBar()

    }

    private fun setupExerciseView(){
        binding?.flProgressBar?.visibility= View.INVISIBLE
        binding?.tvTitle?.visibility = View.INVISIBLE
        binding?.flExerciseView?.visibility= View.VISIBLE
        binding?.tvExercise?.visibility= View.VISIBLE
        binding?.ivImage?.visibility= View.VISIBLE
        binding?.tvupcoming?.visibility= View.INVISIBLE
        binding?.tvnextexercisename?.visibility= View.INVISIBLE

        if(exerciseTimer!=null){
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }

        speakOut(exerciseList!![currentExercisePosition].getName())

        binding?.ivImage?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding?.tvExercise?.text = exerciseList!![currentExercisePosition].getName()

        setExerciseProgressBar()
    }

    private fun setRestProgressBar(){
        binding?.progressBar?.progress = restProgress

        restTimer = object : CountDownTimer(10000,1000){
            override fun onTick(p0: Long) {
                restProgress++
                binding?.progressBar?.progress = 10 - restProgress
                binding?.tvTimer?.text = (10 - restProgress).toString()
            }
            override fun onFinish() {
                currentExercisePosition++
                setupExerciseView()
            }
        }.start()

    }

    private fun setExerciseProgressBar(){
        binding?.progressBarExercise?.progress = exerciseProgress

        exerciseTimer = object : CountDownTimer(30000,1000){
            override fun onTick(p0: Long) {
                exerciseProgress++
                binding?.progressBarExercise?.progress = 30 - exerciseProgress
                binding?.tvTimerExercise?.text = (30 - exerciseProgress).toString()
            }
            override fun onFinish() { if (currentExercisePosition < exerciseList?.size!! - 1) {
                setupRestView()
            } else {

                finish()
                val intent = Intent(this@ExerciseActivity,FinishActivity::class.java)
                startActivity(intent) }
            }
        }.start()

    }
    override fun onDestroy() {
        super.onDestroy()

        if(restTimer != null){
            restTimer?.cancel()
            restProgress = 0
        }

        if(exerciseTimer != null){
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }

        if(tts != null){
            tts!!.stop()
            tts!!.shutdown()
        }

        if(player!= null){
            player!!.stop()
        }

        binding = null
    }

    override fun onInit(status: Int) {
        if (status==TextToSpeech.SUCCESS){
            val result= tts?.setLanguage(Locale.US)

            if ( result== TextToSpeech.LANG_MISSING_DATA || result== TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS","The language specified is not supported")
            }
        }else{
            Log.e("TTS","Initialization Failed!")
        }

    }

    private fun speakOut(text: String){
        tts!!.speak(text,TextToSpeech.QUEUE_FLUSH,null,"")
    }
}