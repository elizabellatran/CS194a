package com.example.tipcalculator

import android.animation.ArgbEvaluator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        seekBarTip.progress = INITIAL_TIP_PERCENT
        percentLabel.text = "$INITIAL_TIP_PERCENT%"
        updateTipDescription(INITIAL_TIP_PERCENT)

        seekBarTip.setOnSeekBarChangeListener(object:  SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser:Boolean) {
                Log.i(TAG, "onProgressChanged $progress")
                percentLabel.text = "$progress%"
                updateTipDescription(progress)
                computeTipAndTotal()
                computeSplit()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        editBase.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.i(TAG, "afterTextChange $s")
                computeTipAndTotal()
                computeSplit()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })

        editNumPeople.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.i(TAG, "afterSplitChange $s")
                computeSplit()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        clearButton.setOnClickListener(View.OnClickListener() {
            Log.i(TAG, "CLEAR BUTTON CLICKED")
            seekBarTip.progress = INITIAL_TIP_PERCENT
            percentLabel.text = "$INITIAL_TIP_PERCENT%"
            updateTipDescription(INITIAL_TIP_PERCENT)

            tvTipAmount.text = "0.00"
            tvTotalAmount.text = "0.00"
            tvSplitTotal.text = "0.00"

            editBase.getText().clear()
            editNumPeople.getText().clear()

        })
    }

    private fun updateTipDescription(tipPercent: Int) {
        val TipDescription : String

        when(tipPercent) {
            in 0..9 -> TipDescription = "Poor"
            in 10..14 -> TipDescription = "Acceptable"
            in 15..19 -> TipDescription = "Good"
            in 20..24 -> TipDescription = "Great"
            else -> TipDescription = "Amazing"
        }
        tvTipDescription.text = TipDescription
        val color = ArgbEvaluator().evaluate(
            tipPercent.toFloat() / seekBarTip.max,
            ContextCompat.getColor(this, R.color.colorWorstTip),
            ContextCompat.getColor(this, R.color.colorBestTip)
        ) as Int
        tvTipDescription.setTextColor(color)
    }

    private fun computeTipAndTotal() {
        // Get the value of the base and tip percent
        if(editBase.text.isEmpty()){
            tvTipAmount.text = "0.00"
            tvTotalAmount.text = "0.00"
            return
        }
        val baseAmount = editBase.text.toString().toDouble()
        val tipPercent = seekBarTip.progress
        val tipAmount = baseAmount * tipPercent / 100
        val totalAmount = baseAmount + tipAmount
        tvTipAmount.text = "%.2f".format(tipAmount)
        tvTotalAmount.text = "%.2f".format(totalAmount)
    }

    private fun computeSplit() {
        if(editNumPeople.text.isEmpty() || tvTotalAmount.text.isEmpty() || editNumPeople.text.toString() == "0") {
            tvSplitTotal.text = "0.00"
            return
        }
        val numOfPeople = editNumPeople.text.toString().toInt()
        val splitTotal = tvTotalAmount.text.toString().toDouble() / numOfPeople
        tvSplitTotal.text = "%.2f".format(splitTotal)
    }
}