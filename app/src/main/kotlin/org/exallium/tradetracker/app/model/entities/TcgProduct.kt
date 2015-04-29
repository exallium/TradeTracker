package org.exallium.tradetracker.app.model.entities

public class TcgProduct: Record<TcgProduct>() {
    public var tcgId: Int = -1
    public var high: Float = 0F
    public var mid: Float = 0F
    public var low: Float = 0F
    public var link: String = ""
    public var card: Card? = null
}
