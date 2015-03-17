package org.virtualview.renderer

import android.app.Activity
import android.view.ViewGroup
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
    def parent

    @Before
    void setupView() {
        parent = new LinearLayout(activity.applicationContext)
    }

    @Test
    void replacingView() {
        def view = new TextView(parent.context)
        parent.addView view
        def currentTree = [
                root: [
                        viewName: 'TextView',
                ],
        ]
        def nextTree = [
                root: [
                        viewName: 'EditText',
                ],
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
        def view = new TextView(parent.context)
        parent.addView view
        def currentTree = [
                root: [
                        viewName: 'TextView',
                        attributes: [
                                alpha: 0.5f,
                        ],
                ],
        ]
        def nextTree = [
                root: [
                        viewName: 'TextView',
                        attributes: [
                                alpha: 0.7f,
                                scaleX: 3.0f,
                        ],
                ],
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

    @Test
    void replacingViewChildren() {
        def view = new LinearLayout(parent.context)
        parent.addView view
        view.addView(new TextView(view.context))
        def currentTree = [
                root: [
                        viewName: 'LinearLayout',
                        children: [
                                [
                                        viewName: 'TextView',
                                ],
                        ],
                ],
        ]
        def nextTree = [
                root: [
                        viewName: 'LinearLayout',
                        children: [
                                [
                                        viewName: 'EditText',
                                ],
                                [
                                        viewName: 'Button',
                                ],
                        ],
                ],
        ]
        def options = [
                currentTree: currentTree,
                nextTree: nextTree,
                view: view,
        ]
        ViewGroup rootView = (ViewGroup) Diff.patch(options)
        assert rootView.getChildAt(0).class.name == 'android.widget.EditText'
        assert rootView.getChildAt(1).class.name == 'android.widget.Button'
    }

    @Test
    void removingViewChildren() {
        def view = new LinearLayout(parent.context)
        parent.addView view
        view.addView(new TextView(view.context))
        def currentTree = [
                root: [
                        viewName: 'LinearLayout',
                        children: [
                                [
                                        viewName: 'TextView'
                                ],
                        ],
                ],
        ]
        def nextTree = [
                root: [
                        viewName: 'LinearLayout',
                ],
        ]
        def options = [
                currentTree: currentTree,
                nextTree: nextTree,
                view: view,
        ]
        ViewGroup rootView = (ViewGroup) Diff.patch(options)
        assert rootView.getChildAt(0) == null
    }
}
