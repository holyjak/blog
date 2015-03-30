var React = require("react");
var immstruct = require("immstruct");
var Page = require("./components");

// immstruct is a thin wrapper for Immutable.js allowing to be notified on change
var accessoriesStruct = immstruct({
    "Ladere": [
        {
            guid: "123",
            variationSkuInCart: null,
            selectedProductSku: "12345", // initially selected
            variations: [
                {sku: "12345"},
                {sku: "65432"}
            ]
        },
        {
            guid: "222",
            variationSkuInCart: null,
            selectedProductSku: "22211", // initially selected
            variations: [
                {sku: "22211"}
            ]
        }
    ],
    "Deksel": [
        {
            guid: "d444",
            variationSkuInCart: "1",
            selectedVariationSku: "1",
            selectedProductSku: "2",
            variations: [
                {sku: "1"},
                {sku: "2"}
            ]
        }]
});


function render () {
  // "cursor" is na updatable view into the immutable data structure: 
  React.render(
          <Page data={accessoriesStruct.cursor()}/>,
    document.body);
}

// Re-render on data change; If used in a Store, we'd do this.trigger(data)
accessoriesStruct.on("swap", render);
render();
