package com.virtualview.virtual

class Virtualization {

    static def createNode(type, Map props, ... children) {
        def key = props.get('key')
        props.remove('key')

        def node = new ElementNode(type, props, key, children)

        node.index = 0

        node
    }
}
