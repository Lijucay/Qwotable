package com.lijukay.qwotable

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.recyclerview.widget.LinearLayoutManager
import com.lijukay.core.database.Quote
import com.lijukay.core.database.Wisdom
import com.lijukay.core.utils.QuotesUtil
import com.lijukay.qwotable.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSpinner()
        setRecyclerViewItems("quotes")
    }

    private fun initSpinner() {
        val spinner: AppCompatSpinner = binding.typeSpinner
        val spinnerTypes = listOf(
            getString(R.string.quotes),
            getString(R.string.wisdom)
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerTypes)
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)

        spinner.adapter = adapter
        spinner.onItemSelectedListener = this
    }

    private fun setRecyclerViewItems(type: String) {
        CoroutineScope(Dispatchers.IO).launch {
            QuotesUtil(this@MainActivity.applicationContext).getAndInsert("get", type) { items ->
                if (items.any { type -> type is Quote } || items.any { type -> type is Wisdom }) {
                    val recyclerView = binding.qwotableRecyclerView
                    val adapter = QwotableAdapter(items)
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                    setSubtitle(adapter.itemCount)
                } else {
                    throw IllegalArgumentException("${items.javaClass.simpleName} is not a valid type of element for this fragment")
                }
            }
        }
    }

    private fun setSubtitle(itemCount: Int) {
        val toolbar = binding.ouiToolbar
        toolbar.setExpandedSubtitle("$itemCount Qwotables")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        binding.qwotableRecyclerView.stopScroll()
        when (position) {
            0 -> {
                setRecyclerViewItems("quotes")
            }
            1 -> {
                setRecyclerViewItems("wisdom")
            }
            else -> {
                throw IllegalArgumentException("$position is not a valid selectable position")
            }
        }    }

    override fun onNothingSelected(parent: AdapterView<*>?) { }
}