/*
 * Declarative transformation of a JavaScript data structure
 */
var _ = require("lodash");
var mappings = { "Black": "black" /*, ...*/ };

// transformDefinition:
var productCatalogTransformationDefinition = {
    type: "map",
    singleGroupBy: "productId",
    filter: { type: "TEAPOT" },
    elementValue: {
        productId: "productId",
        variations: {
            selector: "subProducts",
            singleGroupBy: "productNumber",
            elementValue: {
                productNumber: "productNumber",
                name: "name",
                color: "color",
                htmlColor: {
                    value: function(element) { return mappings[element.color]; }
                },
                images: {
                    selector: "additionalDetailsList",
                    filter: function(it) { return it.name.search(/^IMAGE\d+$/) === 0; },
                    elementValue: "value"
                }
            }
        },
        selectedSubProductId: {
            type: "string",
            selector: "subProducts",
            find: function hasRankOne(subProduct){ return _.find(subProduct.dealerDetails, {"dealerGroup": "webshop", "rank": 1}); },
            value: "productNumber"
        }
    }
};

function transform(sourceElement, transformDefinition) {

    //
    // Shorthand forms
    //
    if (typeof transformDefinition === "string") {
        // This is the shorthand style of specifying a definition
        // Ex.: captainName: "crew.captain.name"
        transformDefinition = { value: transformDefinition };
    }
    if (_.isArray(transformDefinition)) {
        // This is shorthand for { value: [..] }
        transformDefinition = { value: transformDefinition };
    }

    //
    // Processing of properties
    //
    if (_.has(transformDefinition, "literal")) {
        return transformDefinition.literal;
    }

    var actualSourceElement = sourceElement;

    // 1. apply the selector if any
    var selector = transformDefinition.selector;
    if (selector) {
        actualSourceElement = applySelector(sourceElement, selector);
    }

    // 2. filter if any
    if (transformDefinition.filter) {
        actualSourceElement = _.filter(actualSourceElement, transformDefinition.filter);
    }

    // 3. group if any
    var groupBy = transformDefinition.groupBy;
    if (groupBy) {
        actualSourceElement = _.groupBy(actualSourceElement, groupBy);
    }

    var singleGroupBy = transformDefinition.singleGroupBy;
    if (singleGroupBy) {
        actualSourceElement = _.chain(actualSourceElement)
            .groupBy(singleGroupBy)
            .mapValues(_.first)
            .value();
    }

    // 4. find
    if (transformDefinition.find) {
        if (_.isArray(transformDefinition.value)) {
            throw new Error("find produces a single value so 'value' may not be an " +
                            "array transformation expression. TransformDefinition: " +
                            toString(transformDefinition));
        }
        actualSourceElement = _.find(actualSourceElement, transformDefinition.find);
    }

    return transformValue(actualSourceElement, transformDefinition.value, transformDefinition);
}

function applySelector(sourceElement, selector){
    if (typeof selector === "string") {
        return getProperty(sourceElement, selector);
    }
    if (typeof selector === "function") {
        return selector(sourceElement);
    }
    throw new Error("Unsupported selector type " +
                    (typeof selector) + " with the value " + selector);

}

function toString(object){
    var objectFailsafe = (typeof object === "undefined")? "undefined" : object;
    return _.trunc(JSON.stringify(objectFailsafe), 250);
}

function getProperty(object, propertyChain){
    var propNames = propertyChain.split(".");
    return _.reduce(
        propNames,
        function(obj, prop) { return _.result(obj, prop, null); },
        object
    );
}

function transformValue(sourceElement, valueExpression, transformDefinition){
    if (typeof valueExpression === "string") {
        return getProperty(sourceElement, valueExpression);
    }
    if (transformDefinition.elementValue) {
        // Apply to each value of the sourceElement array/map
        var itemTransformDefinition = {
            // Tell ourselves not to check value being array, the check is only required for
            // top-level transformation
            _isItemTransformation: true,
            value: transformDefinition.elementValue
        };
        var sourceItemTransformFn = function(sourceElementItem){
            return transform(sourceElementItem, itemTransformDefinition);
        };

        if (_.isArray(sourceElement)) {
            return _.map(sourceElement, sourceItemTransformFn);
        } else {
            // sourceElement is a 'map' (dictionary) so we transform the values
            // leaving the keys untouched:
            return _.mapValues(sourceElement, sourceItemTransformFn);
        }
    }
    if (_.isArray(valueExpression)) {
        // array of literal objects, process those recursively
        var valueItemTransformFn = function(valueExpressionItem){
            return transform(sourceElement, { value: valueExpressionItem });
        };
        return _.map(valueExpression, valueItemTransformFn);
    }
    if (typeof valueExpression === "object") {
        // Literal object, preserve the keys but expect the values to
        // be also transformDefinitions and thus transform them
        return _.mapValues(valueExpression, _.partial(transform, sourceElement));
    }
    if (typeof valueExpression === "function") {
        return valueExpression(sourceElement);
    }

    throw new Error("Unsupported type of valueExpression: " +
                    (typeof valueExpression) + ", value: " +
                    toString(valueExpression));
}

module.exports = {
    transform: _.partialRight(transform, productCatalogTransformationDefinition)
};
