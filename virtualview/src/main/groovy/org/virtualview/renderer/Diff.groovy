package org.virtualview.renderer

import android.view.View
import android.view.ViewGroup

class Diff {

    static def patch(options) {
        def context = [
                view: options.view,
                isRoot: true,
                rootView: options.view,
        ]
        diff(options.currentTree.root, options.nextTree.root, context)
        context.rootView
    }

    private static def diff(current, next, context) {
        if (current.viewName != next.viewName) {
            replace(current, next, context)
            return
        }

        diffAttributes(current, next, context)
        diffChildren(current, next, context)
    }

    private static def diffChildren(current, next, context) {
        def currentChildren = current.children ?: []
        def nextChildren = next.children ?: []
        def container = context.container
        def count = Math.max((int) currentChildren.size(), (int) nextChildren.size())

        for (def i = 0; i < count; i++) {
            def left = currentChildren[i]
            def right = nextChildren[i]

            if (left == null) {
                container.children[i] = [view: right.clone()]
                continue
            }

            if (right == null) {
                container.children.removeAt(i)
                i--
                count--
                continue
            }

            if (container.children[i] == null) {
                container.children[i] = [:]
            }

            diff(left, right, [
                    view: container.children[i],
                    isRoot: false,
            ])
        }
    }

    private static def diffAttributes(current, next, context) {
        def currentAttrs = current.attributes
        def nextAttrs = next.attributes

        nextAttrs.each {
            if (!currentAttrs.containsKey(it.key) || currentAttrs.get(it.key) != it.value) {
                context.view."$it.key" = it.value
            }
        }

        currentAttrs.each {
            if (!nextAttrs.containsKey(it.key)) {
                // TODO: reset view attribute
            }
        }
    }

    private static def replace(current, next, context) {
        View view = context.view
        def parent = view.parent
        ((ViewGroup) parent)?.removeView view
        def newView = (View) Class.forName("android.widget.${next.viewName}").newInstance(view.context)
        ((ViewGroup) parent)?.addView newView
        if (context.isRoot) {
            context.rootView = newView
        }
    }
}
