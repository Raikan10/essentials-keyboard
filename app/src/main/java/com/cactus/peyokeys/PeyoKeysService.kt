package com.cactus.peyokeys

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.inputmethodservice.InputMethodService
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.LinearLayout

class PeyoKeysService : InputMethodService() {

    private var isShifted = false
    private val letterButtons = mutableMapOf<Char, Button>()

    companion object {
        private const val TAG = "PeyoKeysService"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate() called")
    }

    override fun onCreateInputView(): View {
        Log.d(TAG, "onCreateInputView() called")
        return createKeyboardView()
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        Log.d(TAG, "onStartInput() called - inputType: ${attribute?.inputType}")
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        Log.d(TAG, "onStartInputView() called")
    }

    override fun onEvaluateFullscreenMode(): Boolean {
        Log.d(TAG, "onEvaluateFullscreenMode() called")
        return false
    }

    override fun onEvaluateInputViewShown(): Boolean {
        super.onEvaluateInputViewShown()
        Log.d(TAG, "onEvaluateInputViewShown() called")
        return true  // Always show the input view
    }

    private fun createKeyboardView(): View {
        Log.d(TAG, "createKeyboardView() - Creating keyboard layout")

        val view = layoutInflater.inflate(R.layout.keyboard, null)

        // Get row containers
        val row1 = view.findViewById<LinearLayout>(R.id.row1)
        val row2 = view.findViewById<LinearLayout>(R.id.row2)
        val row3 = view.findViewById<LinearLayout>(R.id.row3)
        val row4 = view.findViewById<LinearLayout>(R.id.row4)

        // Row 1: Q W E R T Y U I O P
        listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P").forEach { letter ->
            row1.addView(createKey(letter))
        }

        // Row 2: A S D F G H J K L
        listOf("A", "S", "D", "F", "G", "H", "J", "K", "L").forEach { letter ->
            row2.addView(createKey(letter))
        }

        // Row 3: Shift + Z X C V B N M + Delete
        row3.addView(createSpecialKey("⇧", 1.5f, false) {
            isShifted = !isShifted
            updateLetterCase()
        })
        listOf("Z", "X", "C", "V", "B", "N", "M").forEach { letter ->
            row3.addView(createKey(letter))
        }
        row3.addView(createSpecialKey("⌫", 1.5f, false) {
            currentInputConnection?.deleteSurroundingText(1, 0)
        })

        // Row 4: Space + Microphone + Return
        row4.addView(createSpecialKey("space", 4f, false) {
            currentInputConnection?.commitText(" ", 1)
        })
        row4.addView(createSpecialKey("🎤", 1.5f, false) {
            handleVoiceInput()
        })
        row4.addView(createSpecialKey("return", 2f, true) {
            currentInputConnection?.commitText("\n", 1)
        })

        Log.d(TAG, "Keyboard layout created")
        return view
    }

    private fun createKey(letter: String): Button {
        val button = Button(this).apply {
            text = letter
            setTextColor(Color.BLACK)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            isAllCaps = false
            setPadding(0, 0, 0, 0)
            stateListAnimator = null
            val margin = dpToPx(3)
            layoutParams = LinearLayout.LayoutParams(
                0,
                dpToPx(50),
                1f
            ).apply {
                setMargins(margin, margin, margin, margin)
            }

            background = createRoundedBackground(Color.WHITE, dpToPx(5))
            elevation = dpToPx(1).toFloat()

            setOnClickListener {
                val textToInsert = if (isShifted) letter.uppercase() else letter.lowercase()
                currentInputConnection?.commitText(textToInsert, 1)
                if (isShifted) {
                    isShifted = false
                    updateLetterCase()
                }
            }
        }

        button.setTextColor(Color.BLACK)

        if (letter.length == 1 && letter[0].isLetter()) {
            letterButtons[letter[0].uppercaseChar()] = button
        }

        return button
    }

    private fun createSpecialKey(label: String, weight: Float, isBlue: Boolean, onClick: () -> Unit): Button {
        val button = Button(this).apply {
            text = label
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            isAllCaps = false
            setPadding(0, 0, 0, 0)
            stateListAnimator = null
            val margin = dpToPx(3)
            layoutParams = LinearLayout.LayoutParams(
                0,
                dpToPx(50),
                weight
            ).apply {
                setMargins(margin, margin, margin, margin)
            }

            if (isBlue) {
                setTextColor(Color.WHITE)
                background = createRoundedBackground(Color.parseColor("#007AFF"), dpToPx(5))
            } else {
                setTextColor(Color.BLACK)
                background = createRoundedBackground(Color.parseColor("#CCCCCC"), dpToPx(5))
            }
            elevation = dpToPx(1).toFloat()

            setOnClickListener { onClick() }
        }

        if (isBlue) {
            button.setTextColor(Color.WHITE)
        } else {
            button.setTextColor(Color.BLACK)
        }

        return button
    }

    private fun updateLetterCase() {
        letterButtons.forEach { (char, button) ->
            button.text = if (isShifted) char.uppercase() else char.lowercase()
            button.setTextColor(Color.BLACK)
        }
    }

    private fun createRoundedBackground(color: Int, cornerRadius: Int): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            setCornerRadius(cornerRadius.toFloat())
        }
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    private fun handleVoiceInput() {
        Log.d(TAG, "Voice input button pressed")
    }
}
