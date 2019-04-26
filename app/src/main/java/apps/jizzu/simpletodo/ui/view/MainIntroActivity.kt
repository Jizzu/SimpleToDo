package apps.jizzu.simpletodo.ui.view

import android.os.Bundle
import apps.jizzu.simpletodo.R
import com.heinrichreimersoftware.materialintro.app.IntroActivity
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide

class MainIntroActivity : IntroActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addSlide(SimpleSlide.Builder()
                .title("Plan your day")
                .description("Organize your tasks and don't forget about important things!")
                .image(R.drawable.illustration_intro_1)
                .background(R.color.white)
                .backgroundDark(R.color.white)
                .layout(R.layout.activity_main_intro)
                .build())

        addSlide(SimpleSlide.Builder()
                .title("Manage your tasks easily")
                .description("Use left/right swipes to remove task and up/down to change it's position in the list")
                .image(R.drawable.illustration_intro_2)
                .background(R.color.white)
                .backgroundDark(R.color.white)
                .layout(R.layout.activity_main_intro)
                .build())

        addSlide(SimpleSlide.Builder()
                .title("Add task using voice")
                .description("Add new tasks quickly using the voice input by long press the + button")
                .image(R.drawable.illustration_intro_3)
                .background(R.color.white)
                .backgroundDark(R.color.white)
                .layout(R.layout.activity_main_intro)
                .build())

        addSlide(SimpleSlide.Builder()
                .title("Get started")
                .description("Get rid of procrastination and improve your productivity")
                .image(R.drawable.illustration_intro_4)
                .background(R.color.white)
                .backgroundDark(R.color.white)
                .layout(R.layout.activity_main_intro)
                .build())
    }
}