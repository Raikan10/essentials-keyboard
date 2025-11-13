package com.cactus.peyokeys

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cactus.CactusContextInitializer
import com.cactus.CactusSTT
import com.cactus.VoiceModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var stt: CactusSTT
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VoiceModelAdapter
    private var whisperModels: List<VoiceModel> = emptyList()

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CactusContextInitializer.initialize(this)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_view_models)
        recyclerView.layoutManager = LinearLayoutManager(this)
        stt = CactusSTT()
        loadVoiceModels()
    }

    private fun loadVoiceModels() {
        lifecycleScope.launch {
            try {
                whisperModels = stt.getVoiceModels()
                val activeModelSlug = VoiceModelPreferences.getActiveModel(this@MainActivity)

                adapter = VoiceModelAdapter(
                    models = whisperModels,
                    selectedModelSlug = activeModelSlug,
                    onDownloadClick = { model, position ->
                        onDownloadModel(model)
                    },
                    onModelSelected = { model ->
                        onModelSelected(model)
                    }
                )
                recyclerView.adapter = adapter
                Log.d(TAG, "Loaded ${whisperModels.size} voice models, active: $activeModelSlug")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading voice models", e)
            }
        }
    }

    private fun onModelSelected(model: VoiceModel) {
        Log.d(TAG, "Model selected: ${model.slug}")
        VoiceModelPreferences.setActiveModel(this, model.slug)
    }

    private fun onDownloadModel(model: VoiceModel) {
        Log.d(TAG, "Download requested for model: ${model.slug}")
        lifecycleScope.launch {
            try {
                adapter.setDownloading(model.slug, true)
                stt.download(model = model.slug)
                adapter.updateModelDownloaded(model.slug)
                Log.d(TAG, "Download completed for model: ${model.slug}")
            } catch (e: Exception) {
                Log.e(TAG, "Error downloading model: ${model.slug}", e)
                adapter.setDownloading(model.slug, false)
            }
        }
    }
}
