package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var equalsWasLast : Boolean = false



    /*
    Declaring Views Variables
     */
    var wholeEquation : String=""
    var lastPressedWasOperator : Boolean=false
    var listofNumbers = mutableListOf<Float>()
    var listofOperators = mutableListOf<Char>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }
//For Performing Each operation on by one
    fun performSingleOperations( num1: Float, num2: Float, operator: Char): Float {
        return when (operator) {
            '+' -> num1 + num2
            '-' -> num1 - num2
            'x' -> num1 * num2
            '/' -> num1 / num2
            else -> {
                Toast.makeText(this, "Invalid Operator", Toast.LENGTH_SHORT).show()
                return 0.0F
            }
        }
    }
/*For performing DMAS operation Left to Right
In mathematical operations the number of operators in an equation is one less the number of digits in the equation(considering binary operations only).
The Lists are made in such a way that when an operator is encountered the operation is to be performed on the two numbers
one having the same index number as the operator and the other having the next index number on the numbers list
After operation is performed result is being saved back in the list and the operator is being removed from the list*/
    fun performDMAS() {
        var i = 0
        while (i < listofOperators.size) {
            if (listofOperators[i] in setOf('/','x')) {
                listofNumbers[i] = performSingleOperations(listofNumbers[i], listofNumbers[i + 1], listofOperators[i])
                listofOperators.removeAt(i)
                listofNumbers.removeAt(i + 1)
            } else {
                i++
            }
        }

        i = 0

        while (i < listofOperators.size) {
            if (listofOperators[i] in setOf('+','-')) {
                listofNumbers[i] = performSingleOperations(listofNumbers[i], listofNumbers[i + 1], listofOperators[i])
                listofOperators.removeAt(i)
                listofNumbers.removeAt(i + 1)
            } else {
                i++
            }
        }
        i=0
        if (listofNumbers.isNotEmpty()) {
            binding.calcDisplayTV.text = listofNumbers[0].toString()
        } else {
            binding.calcDisplayTV.text = "0"
        }
    }


    fun numberAction(view: View) {
            wholeEquation += (view as Button).text.toString()
            binding.calcDisplayTV.text = wholeEquation
            lastPressedWasOperator = false
    }

    fun operationAction(view: View) {
        if(!lastPressedWasOperator){
            wholeEquation += (view as Button).text.toString()
            binding.calcDisplayTV.text = wholeEquation
            lastPressedWasOperator = true
        }
    }
    fun backSpaceAction(view: View){
        if(equalsWasLast){
            wholeEquation = binding.equationTV.text.toString()
            equalsWasLast = false
        }
        if (wholeEquation.isNotEmpty()) {
            wholeEquation = wholeEquation.substring(0, wholeEquation.length - 1)
        }
        binding.calcDisplayTV.text = wholeEquation
    }

//On pressing Equals Button the whole equation is parsed into numbers and operators and stored in lists
    fun equalsAction(view: View) {
        println("EqualsStarted")
        Log.d("=before", "equalsAction:started ")
        if(wholeEquation.isNotEmpty()&& !lastPressedWasOperator){
            var tempstring : String =wholeEquation
            var i: Int=0
            while (i in 0 until tempstring.length){
                if(tempstring[i] in setOf('+' ,'-' ,'x' ,'/')){

                    try {
                        listofNumbers.add(tempstring.substring(0, i).toFloat())
                    } catch (e: NumberFormatException) {
                        Toast.makeText(this, "Invalid number format before operator", Toast.LENGTH_SHORT).show()
                        return
                    }
                    listofOperators.add(tempstring[i])
                    tempstring = tempstring.substring(i+1)
                    i=0
                }
                else{
                    i++
                }


                if (tempstring.isEmpty()) {
                    Toast.makeText(this, "Empty input after operator", Toast.LENGTH_SHORT).show()
                    return
                }
            }

            if (tempstring.isNotEmpty()) {
                try {
                    listofNumbers.add(tempstring.toFloat())
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "Invalid final number format", Toast.LENGTH_SHORT).show()
                }
                performDMAS()
            } else {
                Toast.makeText(this, "Final input is empty", Toast.LENGTH_SHORT).show()
            }
            binding.equationTV.text = wholeEquation
            listofNumbers.clear()
            listofOperators.clear()

        }
        else{
            Toast.makeText(this, "Please perform a right calculation", Toast.LENGTH_SHORT).show()
        }
        equalsWasLast = true
    }

    fun allClearAction(view: View){
        listofNumbers.clear()
        listofOperators.clear()
        wholeEquation = ""
        binding.calcDisplayTV.text = "0"
        binding.equationTV.text = ""
    }



}