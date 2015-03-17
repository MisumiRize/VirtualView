package org.virtualview.virtual

class ElementNode {
    String type, tagName
    Map attributes
    def key
    def children
    int id
    int index

    def ElementNode(tagName, Map attributes, key, children) {
        this.type = 'element'
        this.attributes = attributes
        this.tagName = tagName
        this.children = children
        this.key = key
    }
}
