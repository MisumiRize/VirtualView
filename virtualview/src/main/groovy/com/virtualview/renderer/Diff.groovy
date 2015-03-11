package com.virtualview.renderer

class Diff {

    static def patch(options) {
        diff(options.currentTree.root, options.nextTree.root, [
                container: options.container,
        ])
    }

    private static def diff(current, next, context) {
        if (current.type != next.type) {
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
                container[i] = right.clone()
                continue
            }

            if (right == null) {
                container.removeAt(i)
                i--
                count--
                continue
            }

            if (container.children[i] == null) {
                container.children[i] = [:]
            }

            diff(left, right, [
                    container: container.children[i],
            ])
        }
    }

    private static def diffAttributes(current, next, context) {
        def currentAttrs = current.attributes
        def nextAttrs = next.attributes

        nextAttrs.each {
            if (!currentAttrs.containsKey(it.key) || currentAttrs.get(it.key) != it.value) {
                context.container."$it.key" = it.value
            }
        }

        currentAttrs.each {
            if (!nextAttrs.containsKey(it.key)) {
                context.container.remove(it.key)
            }
        }
    }

    private static def replace(current, next, context) {
        def container = context.container
        container.view = next.clone()
    }
}
