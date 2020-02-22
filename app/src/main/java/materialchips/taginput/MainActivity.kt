package materialchips.taginput

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var context: Context
    private lateinit var chipViewModel: ChipViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this
        val allTags = listOf("Love", "Passion", "Peace", "Hello", "Test")
        chipViewModel = ViewModelProvider(this).get(ChipViewModel::class.java)

// use ViewModel to maintain ui state - internal lateinit var mainTags: MutableList<String>
//        chipViewModel.mainTags = item.mainTags?.toMutableList() ?: mutableListOf("Hello")
        chipViewModel.mainTags = mutableListOf("Hello")
        loadTagsUi(mainTagAutoCompleteTextView, mainTagChipGroup, chipViewModel.mainTags, allTags)
    }

    private fun loadTagsUi(
        autoCompleteTextView: AutoCompleteTextView,
        chipGroup: ChipGroup,
        currentTags: MutableList<String>,
        allTags: List<String>
    ) {

        val adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            allTags
        )
        autoCompleteTextView.setAdapter(adapter)

        fun addTag(name: String) {
            if (name.isNotEmpty() && !currentTags.contains(name)) {
                addChipToGroup(name, chipGroup, currentTags)
                currentTags.add(name)
            } else {
                Log.d("Invalid tag:", name)
            }
        }

        // select from auto complete
        autoCompleteTextView.setOnItemClickListener { adapterView, _, position, _ ->
            autoCompleteTextView.text = null
            val name = adapterView.getItemAtPosition(position) as String
            addTag(name)
        }

        // done keyboard button is pressed
        autoCompleteTextView.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val name = autoCompleteTextView.text.toString()
                textView.text = null
                addTag(name)
                return@setOnEditorActionListener true
            }
            false
        }

        // space or comma is detected
        autoCompleteTextView.addTextChangedListener {
            if (it != null && it.isEmpty()) {
                return@addTextChangedListener
            }

            if (it?.last() == ',' || it?.last() == ' ') {
                val name = it.substring(0, it.length - 1)
                addTag(name)

                mainTagAutoCompleteTextView.text = null
                // mainTagAutoCompleteTextView.removeTextChangedListener(this)
            }
        }

        // initialize
        for (tag in currentTags) {
            addChipToGroup(tag, mainTagChipGroup, currentTags)
        }
    }

    private fun addChipToGroup(name: String, chipGroup: ChipGroup, items: MutableList<String>) {
        val chip = Chip(context)
        chip.text = name
        chip.isClickable = true
        chip.isCheckable = false
        chip.isCloseIconVisible = true

        chipGroup.addView(chip)

        chip.setOnCloseIconClickListener {
            chipGroup.removeView(chip)
            items.remove(name)
        }
    }
}
