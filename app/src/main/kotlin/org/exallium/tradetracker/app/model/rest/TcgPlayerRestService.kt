package org.exallium.tradetracker.app.model.rest

import org.exallium.tradetracker.app.model.entities.TcgProduct
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementArray
import org.simpleframework.xml.Root
import retrofit.http.GET
import retrofit.http.Query
import rx.Observable
import java.util.LinkedList

public trait TcgPlayerRestService {

    [Root(name = "properties")]
    public class TcgProductList {
        [Element(name = "product")]
        public var product: TcgProduct? = null
    }

    [GET("/p")]
    public fun getPriceForCard([Query("pk")] partnerKey: String, [Query("s")] setName: String, [Query("p")] productName: String) : TcgProductList

    //[GET("")]
    //public fun getBulkPriceData([Query("pk")] partnerKey: String): Observable<TcgProduct>
}
