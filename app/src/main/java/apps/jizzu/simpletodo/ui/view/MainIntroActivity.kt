package apps.jizzu.simpletodo.ui.view

import android.os.Bundle
import apps.jizzu.simpletodo.R
import com.heinrichreimersoftware.materialintro.app.IntroActivity
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide

class MainIntroActivity : IntroActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addSlide(SimpleSlide.Builder()
                .title(getString(R.string.intro_1_title))
                .description(getString(R.string.intro_1_description))
                .image(R.drawable.illustration_intro_1)
                .background(R.color.white)
                .backgroundDark(R.color.white)
                .layout(R.layout.activity_main_intro)
                .build())

        addSlide(SimpleSlide.Builder()
                .title(getString(R.string.intro_2_title))
                .description(getString(R.string.intro_2_description))
                .image(R.drawable.illustration_intro_2)
                .background(R.color.white)
                .backgroundDark(R.color.white)
                .layout(R.layout.activity_main_intro)
                .build())

        addSlide(SimpleSlide.Builder()
                .title(getString(R.string.intro_3_title))
                .description(getString(R.string.intro_3_description))
                .image(R.drawable.illustration_intro_3)
                .background(R.color.white)
                .backgroundDark(R.color.white)
                .layout(R.layout.activity_main_intro)
                .build())

        addSlide(SimpleSlide.Builder()
                .title(getString(R.string.intro_4_title))
                .description(getString(R.string.intro_4_description))
                .image(R.drawable.illustration_intro_4)
                .background(R.color.white)
                .backgroundDark(R.color.white)
                .layout(R.layout.activity_main_intro)
                .build())
    }
}