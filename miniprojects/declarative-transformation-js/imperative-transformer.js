var _ = require("lodash");
var mappings = { "Black": "black" /*, ...*/ };

function imperativeTransform(data) {
    return _.chain(data)
        .groupBy("productId")
        .mapValues(_.flow(_.first, transformProduct))
        .value();
}

function transformProduct(product) {
    var variations = _.chain(product.subProducts)
            .groupBy("productNumber")
            .mapValues(_.flow(_.first, transformSubProduct))
            .value();
    return {
        productId: product.productId,
        variations: variations,
        selectedSubProductId: _.chain(variations)
            .find({_rank: 1})
            .result("productNumber")
            .value()
    };
}

function transformSubProduct(subProduct) {
    return {
        productNumber: subProduct.productNumber,
        name: subProduct.name,
        color: subProduct.color,
        htmlColor: mappings[subProduct.color],
        images: _.chain(subProduct.additionalDetailsList)
            .filter(function(it) { return it.name.search(/^IMAGE\d+$/) === 0; })
            .map("value"),
        _rank: _.result(_.find(subProduct.dealerDetails, {"dealerGroup": "webshop"}), "rank")
    };
}

module.exports = {
    transform: imperativeTransform
};
