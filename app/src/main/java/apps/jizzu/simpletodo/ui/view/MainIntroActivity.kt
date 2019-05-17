package apps.jizzu.simpletodo.ui.view

import android.os.Bundle
import apps.jizzu.simpletodo.R
import com.heinrichreimersoftware.materialintro.app.IntroActivity
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide

class MainIntroActivity : IntroActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addIntroScreen(getString(R.string.intro_1_title), getString(R.string.intro_1_description),
                R.drawable.illustration_intro_1)
        addIntroScreen(getString(R.string.intro_2_title), getString(R.string.intro_2_description),
                R.drawable.illustration_intro_2)
        addIntroScreen(getString(R.string.intro_3_title), getString(R.string.intro_3_description),
                R.drawable.illustration_intro_3)
        addIntroScreen(getString(R.string.intro_4_title), getString(R.string.intro_4_description),
                R.drawable.illustration_intro_4)
    }

    private fun addIntroScreen(title: String, description: String, drawable: Int) {
        addSlide(SimpleSlide.Builder()
                .title(title)
                .description(description)
                .image(drawable)
                .background(R.color.white)
                .backgroundDark(R.color.white)
                .layout(R.layout.activity_main_intro)
                .build())
    }
}