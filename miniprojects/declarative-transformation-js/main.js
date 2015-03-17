/*
 * Declarative transformation of a JavaScript data structure
 */
var _ = require("lodash");
var declarativeTransformer = require("./declarative-transformer");
var imperativeTransformer = require("./imperative-transformer");

// INPUT DATA
var rawProductData = [
    {
        productId: "42",
        type: "TEAPOT",
        subProducts: [
            {
                productNumber: "ch132",
                name: "Kyusu Teapot",
                color: "Black",
                additionalDetailsList: [
                    { name: "IMAGE1", value: "kyusu-front.png" },
                    { name: "IMAGE2", value: "kyusu-top.png" },
                    { name: "ORIGIN", value: "Japan" }
                ],
                dealerDetails: [
                    { dealerGroup: "webshop", rank: 1 }
                ]
            }
        ]
    }
];

// // EXPECTED OUTPUT:
// {
//   "42": {
//     "productId": "42",
//     "variations": {
//       "ch132": {
//         "productNumber": "ch132",
//         "name": "Kyusu Teapot",
//         "color": "Black",
//         "htmlColor": "black",
//         "images": [
//           "kyusu-front.png",
//           "kyusu-top.png"
//         ]
//       }
//     },
//     "selectedSubProductId": "ch132"
//   }
// }


console.log(">>> IMPERATIVE:", JSON.stringify(imperativeTransformer.transform(rawProductData), null, 2));

console.log(">>> DECLARATIVE:", JSON.stringify(
    declarativeTransformer.transform(rawProductData), null, 2));
