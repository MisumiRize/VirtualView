package org.virtualview.renderer

import android.app.Activity
import android.widget.LinearLayout
import android.widget.TextView
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@Config(emulateSdk = 18, manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
class DiffTest {

    def activity = Robolectric.setupActivity Activity
    def view

    @Before
    void setupView() {
        def parent = new LinearLayout(activity.applicationContext)
        view = new TextView(parent.context)
        parent.addView view
    }

    @Test
    void replacingView() {
        def currentTree = [
                root: [
                        viewName: 'TextView',
                ]
        ]
        def nextTree = [
                root: [
                        viewName: 'EditText',
                ]
        ]
        def options = [
                currentTree: currentTree,
                nextTree: nextTree,
                view: view,
        ]
        def rootView = Diff.patch options
        assert rootView.class.name == 'android.widget.EditText'
    }

    @Test
    void replacingViewAttribute() {
        def currentTree = [
                root: [
                        viewName: 'TextView',
                        attributes: [
                                alpha: 0.5f,
                        ]
                ]
        ]
        def nextTree = [
                root: [
                        viewName: 'TextView',
                        attributes: [
                                alpha: 0.7f,
                                scaleX: 3.0f,
                        ]
                ]
        ]
        def options = [
                currentTree: currentTree,
                nextTree: nextTree,
                view: view,
        ]
        def rootView = Diff.patch options
        assert rootView.alpha == 0.7f
        assert rootView.scaleX == 3.0f
    }
}
