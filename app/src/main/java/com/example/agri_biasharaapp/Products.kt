package com.example.agri_biasharaapp

class Products {
    var post_id: String? = null
    var product_name: String? = null
    var product_category: String? = null
    var product_quantity: String? = null
    var product_price: String? = null
    var post_time: String? = null
    var post_image: String? = null
    var product_poster: String? = null

    constructor(product_poster: String?) {
        this.product_poster = product_poster
    }

    constructor(
        post_id: String?,
        product_name: String?,
        product_category: String?,
        product_quantity: String?,
        product_price: String?,
        post_time: String?,
        post_image: String?
    ) {
        this.post_id = post_id
        this.product_name = product_name
        this.product_category = product_category
        this.product_quantity = product_quantity
        this.product_price = product_price
        this.post_time = post_time
        this.post_image = post_image
    }

    constructor() {}
}